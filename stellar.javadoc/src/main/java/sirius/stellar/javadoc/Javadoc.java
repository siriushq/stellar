package sirius.stellar.javadoc;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/// Service client for generating documentation from source files.
///
/// This can be created using the static [#create] method, and is [AutoCloseable].
/// If multiple source files are provided, they are processed in parallel.
/// All source files are expected to be [StandardCharsets#UTF_8] encoded.
public interface Javadoc extends AutoCloseable {

	/// Create a generator instance.
	static Javadoc create() {
		return new DJavadoc();
	}

	/// Add a source file to be read from the provided [Path].
	Javadoc source(Path file);

	/// Add a source file from the provided `byte[]`.
	Javadoc source(byte[] data);

	/// Add a source file from the provided [String].
	Javadoc source(String data);

	/// Add multiple source files to be read from the provided directory [Path]. \
	/// This walks the entire tree for `.java` files, recursively for nested structures.
	Javadoc sources(Path directory);

	/// Change the underlying executor used for parallel generation. \
	/// By default, [Executors#newVirtualThreadPerTaskExecutor()] is used.
	Javadoc executor(ExecutorService executor);

	/// Generates a JSON model to the provided [OutputStream]. \
	/// This will read all registered source files.
	void generate(OutputStream output);

	/// Generates both a JSON model and HTML website to the provided directory [Path]. \
	/// This will generate a `javadoc.json` & `javadoc.html` (which reads from the JSON).
	void generate(Path path);
}