package sirius.stellar.lifecycle.natives;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheConfig;

import static io.jstach.jstache.JStacheType.*;

@JStache(template = """
package {{pakkage}};

import java.io.*;
import java.util.*;
import java.lang.foreign.*;
import java.nio.file.*;
import java.lang.annotation.*;

import static java.lang.foreign.SymbolLookup.*;
import static java.nio.file.Files.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
import static {{pakkage}}.{{identifier}}Lookup.*;

/// Generated lookup stub for `{{stub}}`.
///
/// Use the [#with(Arena)] method to create an instance, and [#close()] this
/// resource _after_ closing the [Arena], or any created temporary files will
/// never be deleted.
///
/// Never use this lookup stub with a non-closeable [Arena], such as
/// [Arena#global], or it will be impossible to safely close this resource.
///
/// @author `sirius.stellar.lifecycle.natives` (generated code)
/// @see {{pakkage}}
@{{identifier}}LookupGenerated("sirius.stellar.lifecycle.natives")
{{header}}
final class {{identifier}}Lookup implements SymbolLookup, AutoCloseable {

	private final SymbolLookup delegate;
	private final Path path;
	private final Arena arena;

	private {{identifier}}Lookup(SymbolLookup delegate, Path path, Arena arena) {
		this.delegate = delegate;
		this.path = path;
		this.arena = arena;
	}

	/// Obtain an instance using the provided arena.
	/// @throws IllegalStateException failed to lookup library
	static {{identifier}}Lookup with(Arena arena) {
		ModuleLayer layer = ModuleLayer.boot();
		Exception cause = null;

		for (Module module : layer.modules()) {
			try (InputStream stream = module.getResourceAsStream("META-INF/natives/{{stub}}")) {
				if (stream == null) continue;

				Path path = createTempFile("{{pakkage}}", System.mapLibraryName("{{stub}}"));
				SymbolLookup delegate = libraryLookup(path, arena);

				return new {{identifier}}Lookup(delegate, path, arena);
			} catch (IllegalStateException
					| WrongThreadException
					| IllegalArgumentException
					| IllegalCallerException
					| IOException exception) {
				cause = exception;
				break;
			}
		}

		if (cause == null) throw new IllegalStateException("Missing {{stub}}");
		throw new IllegalStateException(cause);
	}

	@Override
	public Optional<MemorySegment> find(String name) {
		return this.delegate.find(name);
	}

	@Override
	public void close() throws IOException {
		MemorySegment.Scope scope = this.arena.scope();
		if (scope.isAlive()) throw new AssertionError("Fatal lookup stub closure attempt");

		deleteIfExists(this.path);
	}

	/// Annotation to mark generated code, for code coverage report exclusion.
	///
	/// This is generated due to the variant of `javax.annotation/jakarta.annotation`
	/// requiring an external dependency or `requires` for a JDK to be present.
	///
	/// This should never be used outside [{{identifier}}Lookup].
	@Retention(CLASS)
	@Target(TYPE)
	@interface {{identifier}}LookupGenerated {

		/// The name of the code generator used.
		String value();
	}
}""")
@JStacheConfig(type = STACHE)
record NativesStub(String identifier, String stub, String pakkage, String header) {

	/// Create an instance of [NativesStub].
	static NativesStub of(String stub, String pakkage, String header) {
		return new NativesStub(capitalize(stub), stub, pakkage, header);
	}

	/// Capitalize the first letter of the provided `camelCase` string.
	private static String capitalize(String text) {
		if (text == null || text.isEmpty()) throw new IllegalStateException();
		return text.substring(0, 1).toUpperCase() + text.substring(1);
	}

	/// Return the fully-qualified name to write this stub to.
	String fqn() {
		return this.pakkage + "." + this.identifier + "Lookup";
	}

	/// Generate the [String] source code for this stub.
	String generate() {
		return NativesStubRenderer.of().execute(this);
	}
}