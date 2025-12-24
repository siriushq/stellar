package sirius.stellar.lifecycle.spi;

import sirius.stellar.lifecycle.spi.ModuleInfoReader.Provides;
import sirius.stellar.lifecycle.spi.ModuleInfoReader.Requires;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.NullType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.*;
import static java.util.stream.Collectors.*;
import static javax.lang.model.SourceVersion.*;
import static javax.lang.model.element.ElementKind.*;
import static javax.tools.StandardLocation.*;
import static sirius.stellar.lifecycle.spi.APContext.*;

@SupportedAnnotationTypes({
		ServicePrism.PRISM_TYPE,
		ServiceProviderPrism.PRISM_TYPE
})
public final class ServiceProcessor extends AbstractProcessor {

	private final Map<String, Set<String>> services = new ConcurrentHashMap<>();

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return latestSupported();
	}

	@Override
	public synchronized void init(ProcessingEnvironment environment) {
		super.init(environment);
		APContext.init(environment);
		this.validateJava();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment round) {
		if (round.processingOver()) return processFinal();

		Elements elements = processingEnv().getElementUtils();
		TypeElement providerAnnotation = elements.getTypeElement(ServiceProviderPrism.PRISM_TYPE);

		for (Element element : round.getElementsAnnotatedWith(providerAnnotation)) {
			if (element.getKind() != CLASS) continue;
			if (!(element instanceof TypeElement provider)) continue;

			ServiceProviderPrism prism = requireNonNull(ServiceProviderPrism.getInstanceOn(element));
			boolean infer = prism.value().isEmpty();

			List<? extends TypeMirror> values = !infer ? prism.value() : List.of(this.infer(provider));
			for (TypeMirror mirror : values) {
				if (mirror instanceof NullType) continue;
				String spi = mirror.toString();
				String sp = elements.getBinaryName(provider).toString();
				this.services.computeIfAbsent(spi, key -> new LinkedHashSet<>()).add(sp);
			}
		}
		return false;
	}

	/// Compute the final round of annotation processing.
	private boolean processFinal() {
		try {
			this.write();
			this.validate();
			APContext.clear();
			return false;
		} catch (IOException exception) {
			throw new IllegalStateException(exception);
		}
	}

	/// Infer the SPI for the [Service.Provider] annotation, if [Service.Provider#value()] returns `{}`.
	private TypeMirror infer(TypeElement provider) {
		TypeMirror candidate = this.inferServiceSubclass(provider);
		if (candidate != null) return candidate;

		candidate = this.inferServiceInterface(provider);
		if (candidate != null) return candidate;

		candidate = this.inferInterface(provider);
		if (candidate != null) return candidate;

		candidate = this.inferBasic(provider);
		if (candidate != null) return candidate;

		return processingEnv().getTypeUtils().getNullType();
	}

	/// Infer the SPI for the [Service.Provider] annotation, using the [Service] annotation
	/// on any subclasses if present, or returning `null`.
	private TypeMirror inferServiceSubclass(TypeElement provider) {
		for (TypeMirror candidate : provider.getInterfaces()) {
			if (!(processingEnv().getTypeUtils().asElement(candidate) instanceof TypeElement element)) continue;
			if (ServicePrism.getInstanceOn(element) == null) continue;
			return candidate;
		}
		return null;
	}

	/// Infer the SPI for the [Service.Provider] annotation, using the [Service] annotation
	/// on any implemented interfaces if present, or returning `null`.
	private TypeMirror inferServiceInterface(TypeElement provider) {
		TypeMirror superclass = provider.getSuperclass();
		if (superclass == null || superclass.toString().equals("java.lang.Object")) return null;
		if (!(processingEnv().getTypeUtils().asElement(superclass) instanceof TypeElement element)) return null;
		if (ServicePrism.getInstanceOn(element) == null) return null;
		return superclass;
	}

	/// Infer the SPI for the [Service.Provider] annotation using the first direct interface
	/// implementation if present.
	private TypeMirror inferInterface(TypeElement provider) {
		List<? extends TypeMirror> interfaces = provider.getInterfaces();
		if (!interfaces.isEmpty()) return interfaces.getFirst();
		return null;
	}

	/// Infer the SPI for the [Service.Provider] annotation using the direct superclass,
	/// as long as it is not [java.lang.Object].
	private TypeMirror inferBasic(TypeElement provider) {
		TypeMirror superclass = provider.getSuperclass();
		if (superclass != null && !superclass.toString().equals("java.lang.Object")) return superclass;
		return null;
	}

	/// Write the `META-INF/services` files (during the final processing round).
	/// @throws IOException a fatal error occurred merging with an existing file
	private void write() throws IOException {
		Filer filer = processingEnv().getFiler();
		for (Map.Entry<String, Set<String>> entry : this.services.entrySet()) {
			String spi = entry.getKey();
			String resource = "META-INF/services/" + spi;

			Set<String> sp = new HashSet<>(entry.getValue());
			try (Reader reader = filer.getResource(CLASS_OUTPUT, "", resource).openReader(true)) {
				BufferedReader buffered = new BufferedReader(reader);
				sp.addAll(buffered.lines().collect(toSet()));
			} catch (IOException exception) {
				logNote("Generating fresh META-INF/services for SPI %s", spi);
			}

			Writer writer = null;
			try {
				writer = filer.createResource(CLASS_OUTPUT, "", resource).openWriter();
				writer.write(String.join("\n", sp));
			} catch (FilerException exception) {
				writer = filer.getResource(CLASS_OUTPUT, "", resource).openWriter();
				writer.write(String.join("\n", sp));
			} catch (IOException exception) {
				logWarn("Failed to write SPI '%s' to location '%s'.\n%s", spi, resource, exception.getMessage());
			} finally {
				assert writer != null;
				writer.close();
			}
		}
	}

	/// Validate that any generated SPI provides are also declared in the `module-info`.
	private void validate() {
		moduleInfoReader().ifPresent(reader -> {
			for (Requires require : reader.requires()) {
				ModuleElement dependency = require.getDependency();
				this.validateRequire(require, dependency);
			}
			if (isTestCompilation()) return;
			this.validateMissing(reader.provides(), getProjectModuleElement());
		});
	}

	/// Validate that a given `requires` clause on `sirius.stellar.lifecycle.spi` is `static`.
	private void validateRequire(Requires require, ModuleElement dependency) {
		if (require.isStatic()) return;

		String module = this.getClass().getModule().getName();
		if (module == null) module = "sirius.stellar.lifecycle.spi";
		if (!dependency.getQualifiedName().contentEquals(module)) return;

		logError(dependency, "`requires %s` should be `requires static %<s`;", module);
	}

	/// Validate that there are no missing `provides` clauses, for service provides of the same
	/// module. Errors are logged if any are missing.
	private void validateMissing(List<Provides> provides, ModuleElement module) {
		Map<String, Set<String>> missing = this.services.entrySet()
				.stream()
				.map(entry -> {
					String spi = qualifyDollar(entry.getKey());
					Set<String> sp = entry.getValue()
							.stream()
							.map(this::qualifyDollar)
							.collect(toSet());
					return Map.entry(spi, sp);
				})
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

		for (Provides provide : provides) {
			String spi = qualifyDollar(provide.service());
			Set<String> sp = provide.implementations()
					.stream()
					.map(this::qualifyDollar)
					.collect(toSet());

			if (!missing.containsKey(spi)) continue;
			Set<String> spExpected = missing.get(spi);
			spExpected.removeAll(sp);

			if (!spExpected.isEmpty()) continue;
			missing.remove(spi);
		}

		for (Map.Entry<String, Set<String>> entry : missing.entrySet()) {
			String spi = entry.getKey();
			String sp = String.join(", ", entry.getValue());
			logError(module, "Missing `provides %s with %s;` in module-info.java", spi, sp);
		}
	}

	/// Validate that the user is running at least the minimum Java version to use this processor.
	private void validateJava() {
		int release = processingEnv().getSourceVersion().ordinal();
		if (release < RELEASE_11.ordinal()) throw new IllegalStateException("Java release must be >=11, is " + release);
	}

	/// Replace all `$` in a string with `.`, to provide readable output for inner class references.
	private String qualifyDollar(String value) {
		return value.replace("$", ".");
	}
}