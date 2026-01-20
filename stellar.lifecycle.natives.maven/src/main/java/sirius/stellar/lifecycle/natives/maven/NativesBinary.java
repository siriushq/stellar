package sirius.stellar.lifecycle.natives.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static java.nio.file.FileSystems.newFileSystem;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import static java.util.jar.Attributes.Name.MANIFEST_VERSION;

/// Represents a single `binary` element within `binaries` configuration
/// element, within the configuration of [NativesMojo], for example:
///
/// ```
/// <plugin>...
/// 	<artifactId/>
/// 	<configuration>...
/// 		<binaries>
/// 			<binary>
/// 				<name/>
/// 				<classifier/>
/// 				<fetch/>
/// 				<hash/>
/// ```
public final class NativesBinary implements AutoCloseable {

	@Parameter
	private String name;

	@Parameter
	private String classifier;

	@Parameter
	private String fetch;

	@Parameter
	private String hash;

	/// Writable ZIP filesystem associated with the [#classifier] of this entry.
	private FileSystem system;

	/// Path associated with [#system].
	private Path path;

	/// Name to use for the shared library, which should be the same among
	/// multiple instances (for different targets / classifiers).
	String name() {
		return this.name;
	}

	/// Target / classifier for this specific binary, which would be
	/// appended to the name of the generated JAR archive artifacts.
	String classifier() {
		return this.classifier;
	}

	/// Return the path associated with [#system].
	Path path() {
		requireNonNull(this.path);
		return this.path;
	}

	/// Returns a parsed [URL] from [#fetch], making guarantees about
	/// the protocol being supported by [URL#openConnection()].
	private URL fetchUrl() throws MojoExecutionException {
		try {
			String normalized = this.fetch.replace('\\', '/');
			URL url = URI.create(normalized).toURL();
			return switch (url.getProtocol()) {
				case "http", "https", "file", "ftp" -> url;
				default -> throw new MojoExecutionException("Unsupported URL");
			};
		} catch (IllegalArgumentException | MalformedURLException exception) {
			throw new MojoExecutionException("Invalid URI, or not a URL");
		}
	}

	/// Opens an output stream, for the writable file, for this entry, creating
	/// a file if it does not already exist, and setting [#system] to the result.
	///
	/// @param output The output directory (configured in [NativesMojo]).
	/// @param prefix Output file name prefix e.g. `finalName` of the project.
	void create(Path output, String prefix) {
		try {
			Files.createDirectories(output);
			this.path = output.resolve(Path.of(prefix + "-" + this.classifier));

			Map<String, String> properties = Map.of("create", "true");
			URI uri = URI.create("jar:file:" + this.path.toUri().getPath());
			this.system = newFileSystem(uri, properties);
		} catch (IOException exception) {
			throw new IllegalStateException(exception);
		}
	}

	/// Whether this binary already exists in the output stream created by
	/// [#create], using the constant [#hash] to assert this.
	boolean inserted() {
		byte[] binary;

		try {
			Path path = this.system.getPath("META-INF/natives/", this.name);
			binary = Files.readAllBytes(path);
		} catch (IOException exception) {
			return false;
		}

		try {
			String algorithm = switch (this.hash.length()) {
				case 32 -> "MD5";
				case 40 -> "SHA-1";
				case 64 -> "SHA-256";
				case 96 -> "SHA-384";
				case 128 -> "SHA-512";
				default -> throw new IllegalStateException("Bad hash");
			};

			MessageDigest digest = MessageDigest.getInstance(algorithm);
			byte[] expected = HexFormat.of().parseHex(this.hash);
			byte[] found = digest.digest(binary);

			return Arrays.equals(expected, found);
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException(exception);
		}
	}

	/// Insert this binary into the output stream created by [#create].
	void insert() throws MojoExecutionException {
		try (InputStream stream = this.fetchUrl()
				.openConnection()
				.getInputStream()) {
			Path binary = this.system.getPath("META-INF/natives/", this.name);
			Files.createDirectories(binary.getParent());
			Files.copy(stream, binary);

			Path manifest = this.system.getPath("META-INF/MANIFEST.MF");
			Files.createDirectories(manifest.getParent());
			this.manifest(Files.newOutputStream(manifest));
		} catch (IOException exception) {
			throw new IllegalStateException(exception);
		}
	}

	/// Write a `MANIFEST.MF` file to the provided [OutputStream].
	/// This also closes the provided stream.
	private void manifest(OutputStream stream) throws IOException {
		try (stream) {
			Manifest manifest = new Manifest();

			Attributes attributes = manifest.getMainAttributes();
			attributes.put(MANIFEST_VERSION, "1.0");

			Attributes.Name createdBy = new Attributes.Name("Created-By");
			attributes.put(createdBy, this.getClass().getPackageName());

			manifest.write(stream);
		}
	}

	@Override
	public void close() throws MojoExecutionException {
		try {
			if (this.system == null) return;
			this.system.close();
		} catch (IOException exception) {
			throw new MojoExecutionException(exception);
		}
	}

	@Override
	public String toString() {
		String contents = new StringJoiner(", ")
			.add("name='" + this.name + '\'')
			.add("classifier='" + this.classifier + '\'')
			.add("fetch='" + this.fetch + '\'')
			.add("hash='" + this.hash + '\'')
			.add("system='" + this.system + '\'')
			.add("path='" + this.path + '\'')
			.toString();
		return super.toString() + '[' + contents + ']';
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof NativesBinary that)) return false;
		return Objects.equals(this.name, that.name)
				&& Objects.equals(this.classifier, that.classifier)
				&& Objects.equals(this.fetch, that.fetch)
				&& Objects.equals(this.hash, that.hash);
	}

	@Override
	public int hashCode() {
		return hash(this.name, this.classifier, this.fetch, this.hash);
	}
}