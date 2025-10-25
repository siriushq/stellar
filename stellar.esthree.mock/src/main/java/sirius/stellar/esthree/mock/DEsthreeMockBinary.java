package sirius.stellar.esthree.mock;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/// Downloading tool that is run from the Maven build lifecycle, in order to
/// supply a binary accessible from [EsthreeMockBinary].
public final class DEsthreeMockBinary implements EsthreeMockBinary {

	@Override
	public URL url() {
		URL url = this.getClass().getResource("/META-INF/minio");
		if (url == null) url = this.getClass().getResource("/META-INF/minio.exe");
		if (url == null) throw new IllegalStateException("Cannot locate MinIO executable for EsthreeMockBinary");
		return url;
	}

	@Override
	public InputStream open() throws IOException {
		return url().openStream();
	}

	/// This method is intended to be run from the Maven build lifecycle.
	public static void main(String[] arguments) {
		String output = arguments[0], system = arguments[1], architecture = arguments[2];
		if (output.isEmpty() || system.isEmpty() || architecture.isEmpty()) throw new IllegalStateException();
		if (system.length() < 3) throw new IllegalStateException("Unsupported operating system");

		String minioSystem = switch (system.substring(0, 3).toLowerCase()) {
			case "win" -> "windows";
			case "mac" -> "darwin";
			case "lin", "uni", "aix" -> "linux";
			default -> throw new IllegalStateException("Unsupported operating system");
		};
		String minioArchitecture = switch (architecture.toLowerCase()) {
			case "amd64", "x86_64" -> "amd64";
			case "arm", "armv7l", "aarch32" -> "arm";
			case "arm64", "aarch64" -> "arm64";
			case "ppc64le" -> "ppc64le";
			case "mips64", "mips64el" -> "mips64";
			case "s390x" -> "s390x";
			default -> throw new IllegalStateException("Unsupported processor architecture");
		};
		String minioBinary = minioSystem.equals("windows") ? "minio.exe" : "minio";
		String minioUrl = String.format("https://dl.min.io/community/server/minio/release/%s-%s/%s", minioSystem, minioArchitecture, minioBinary);

		Path binary = Path.of(output)
				.resolve("META-INF/" + minioBinary)
				.normalize();
		if (Files.exists(binary)) return;

		try (InputStream stream = URI.create(minioUrl).toURL().openStream()) {
			Files.copy(stream, binary);
		} catch (IOException exception) {
			throw new IllegalStateException(exception);
		}
	}
}