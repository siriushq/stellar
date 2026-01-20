package sirius.stellar.lifecycle.natives;

import io.avaje.prism.GenerateAPContext;
import io.avaje.prism.GeneratePrism;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static javax.lang.model.SourceVersion.RELEASE_25;
import static javax.lang.model.SourceVersion.latestSupported;
import static sirius.stellar.lifecycle.natives.APContext.logError;
import static sirius.stellar.lifecycle.natives.APContext.processingEnv;

@GenerateAPContext
@GeneratePrism(Natives.class)
@SupportedAnnotationTypes(NativesPrism.PRISM_TYPE)
public final class NativesProcessor extends AbstractProcessor {

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
		if (round.processingOver()) {
			APContext.clear();
			return false;
		}

		Elements elements = processingEnv().getElementUtils();
		TypeElement nativesAnnotation = elements.getTypeElement(NativesPrism.PRISM_TYPE);

		for (Element element : round.getElementsAnnotatedWith(nativesAnnotation)) {
			if (!(element instanceof PackageElement pakkage)) continue;
			String name = pakkage.getQualifiedName().toString();

			NativesPrism prism = requireNonNull(NativesPrism.getInstanceOn(element));
			for (String stub : prism.value()) this.stub(element, stub, name, prism.header());
		}
		return false;
	}

	/// Generates a native lookup stub for the provided named native.
	/// @param element The element (`package-info`) that caused the generation.
	/// @param pakkage The package name to write the stub to.
	/// @param header Extra Java code to inject above generated `class`.
	private void stub(Element element, String stub, String pakkage, String header) {
		NativesStub source = NativesStub.of(stub, pakkage, header);
		Filer filer = processingEnv().getFiler();

		try (Writer writer = filer.createSourceFile(source.fqn(), element).openWriter()) {
			writer.write(source.generate());
		} catch (IOException exception) {
			logError(element, "Failed to generate lookup stub (%s)", exception.getMessage());
		}
	}

	/// Validate that the user is running at least the minimum Java version to use this processor.
	private void validateJava() {
		int release = processingEnv().getSourceVersion().ordinal();
		if (release < RELEASE_25.ordinal()) throw new IllegalStateException("Java release must be >=25, is " + release);
	}
}