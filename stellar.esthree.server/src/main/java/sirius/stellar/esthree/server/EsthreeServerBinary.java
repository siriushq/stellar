package sirius.stellar.esthree.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/// Abstraction to get access to the underlying MinIO binary used by [EsthreeServer].
/// This can be created using the static [#create] method.
public interface EsthreeServerBinary {

	/// Get the URL for the MinIO binary.
	URL url();

	/// Open a stream to read the MinIO binary.
	/// @throws IllegalStateException binary cannot be found
	/// @throws IOException failed to open stream to located binary
	InputStream open() throws IOException;

	/// Create an instance of [EsthreeServerBinary].
	static EsthreeServerBinary create() {
		return new DEsthreeServerBinary();
	}
}