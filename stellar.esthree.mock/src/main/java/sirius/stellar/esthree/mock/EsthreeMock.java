package sirius.stellar.esthree.mock;

import java.nio.file.Path;
import java.util.List;

/// Service client for serving S3. \
/// This can be created using the static [#builder()] method, and is [AutoCloseable].
/// Most methods in [Builder] should not be called more than once, unless documented.
///
/// ```
/// EsthreeMock server = EsthreeMock.builder()
///         .volume("/path/to/working_directory")
///         .console(9090)
///         .build();
///
/// // remember to gracefully shutdown
/// server.close();
/// ```
///
/// @see sirius.stellar.esthree.mock
public interface EsthreeMock extends AutoCloseable {

	/// Return a builder to construct [EsthreeMock] instances with.
	static Builder builder() {
		return new DEsthreeMockBuilder();
	}

	/// @see EsthreeMock
	interface Builder {

		/// Supply a list of multiple volumes (remote nodes, or directories) to connect to.
		/// @see #volume
		Builder volumes(List<String> volumes);

		/// Supply a single volume (remote node or directory) to connect to.
		/// You can chain multiple calls of this method or [#volumes] to add more.
		/// Expansion notation (`{x...y}` to denote a sequential series) is supported here.
		/// @see #volumes
		Builder volume(String volume);

		/// Override all provided volumes and use a temporary directory instead.
		/// This is deleted on graceful shutdown.
		Builder temporaryVolume();

		/// Supply a port to run the embedded console UI on.
		/// By default, a random port is selected.
		Builder console(int port);

		/// Disable the embedded console UI.
		Builder disableConsole();

		/// Provide a path to the certificates directory to use for encryption.
		/// By default, `~/.minio/certs` is used (the user directory).
		Builder certificates(Path directory);

		/// Enable and configure the embedded FTPS (secure) server with the provided port and TLS keys.
		Builder ftps(int port, Path publicKey, Path privateKey);

		/// Enable and configure the embedded SFTP server with the provided port and SSH key.
		Builder sftp(int port, Path privateKey);

		/// Enable quiet logging (disables startup & information messages).
		Builder quiet();

		/// Enable anonymous logging (hide sensitive information from logging).
		Builder anonymous();

		/// Build and return the server (which is an [AutoCloseable]).
		EsthreeMock build();
	}
}