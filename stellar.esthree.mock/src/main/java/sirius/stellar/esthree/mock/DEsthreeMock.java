package sirius.stellar.esthree.mock;

import io.avaje.jsonb.Jsonb;
import org.jspecify.annotations.Nullable;
import sirius.stellar.logging.Logger;
import sirius.stellar.logging.collect.Collector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.*;
import static java.nio.file.attribute.PosixFilePermission.*;
import static sirius.stellar.logging.LoggerLevel.*;

/// Domain implementation of [EsthreeMock].
public final class DEsthreeMock implements EsthreeMock {

	private final Path binary;
	private final Process process;
	private final ProcessHandle handle;
	private final Thread logger;
	private final Jsonb jsonb;

	private final @Nullable Path temporary;

	DEsthreeMock(Map<String, String> environment, List<String> arguments, @Nullable Path temporary) {
		this.temporary = temporary;
		this.jsonb = Jsonb.instance();

		EsthreeMockBinary binary = EsthreeMockBinary.create();
		try (InputStream stream = binary.open()) {
			String executable = Path.of(binary.url().toURI()).getFileName().toString();
			this.binary = Files.createTempFile("esthree-", "-" + executable);
			Files.copy(stream, this.binary, REPLACE_EXISTING);

			if (!executable.contains(".exe")) {
				Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(this.binary);
				permissions.add(OWNER_EXECUTE);
				permissions.add(GROUP_EXECUTE);
				permissions.add(OTHERS_EXECUTE);
				Files.setPosixFilePermissions(this.binary, permissions);
			}

			arguments = Stream.concat(Stream.of(this.binary.toAbsolutePath().toString()), arguments.stream()).toList();
			Logger.information(arguments);
			ProcessBuilder builder = new ProcessBuilder(arguments);
			builder.environment().putAll(environment);
			Logger.information(environment);

			this.process = builder.start();
			this.handle = this.process.toHandle();

			BufferedReader reader = new BufferedReader(new InputStreamReader(this.process.getErrorStream()));
			this.logger = Thread.startVirtualThread(() -> this.log(reader));
		} catch (IOException | URISyntaxException exception) {
			throw new IllegalStateException("Failed to start EsthreeMock", exception);
		}
	}

	/// Dispatches logs to `stellar.logging`.
	private void log(BufferedReader reader) {
		try {
			String line;
			while ((line = reader.readLine()) != null && !this.logger.isInterrupted()) {
				DEsthreeMockMessage message = this.jsonb.type(DEsthreeMockMessage.class).fromJson(line);
				String thread = "EsthreeMock-" + this.process.pid();
				String name = "sirius.stellar.esthree.mock";

				Logger.dispatch(message.time(), message.mappedLevel(), thread, name, message.message());
			}
			Logger.debugging("Closed EsthreeMock process with code {0,number,integer}.", this.process.waitFor());
		} catch (IOException | InterruptedException exception) {
			throw new IllegalStateException("Failed to read line when dispatching logging in EsthreeMock", exception);
		}
	}

	// TODO - remove
	public static void main(String[] arguments) {
		Logger.collector(Collector.console());
		Logger.severity(DEBUGGING.severity());

		EsthreeMock server = EsthreeMock.builder()
				.console(9090)
				.temporaryVolume()
				.build();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				server.close();
				Logger.close();
			} catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}));
	}
	//

	@Override
	public void close() throws IOException, ExecutionException, InterruptedException {
		Logger.debugging("Closing EsthreeMock process" + (this.temporary == null ? " & logger thread." : ", logger thread & temporary directory."));

		this.handle.destroy();
		this.handle.onExit().get();
		this.process.getErrorStream().close();
		this.logger.interrupt();

		Files.deleteIfExists(this.binary);
		if (this.temporary == null) return;

		try (Stream<Path> walk = Files.walk(this.temporary)) {
			for (Path path : walk.toList()) Files.deleteIfExists(path);
		}
	}
}

final class DEsthreeMockBuilder implements EsthreeMock.Builder {

	private final Set<String> arguments;
	private final Set<String> volumes;
	private final Map<String, String> environment;

	private @Nullable Path temporary;

	DEsthreeMockBuilder() {
		this.arguments = new LinkedHashSet<>();
		this.volumes = new LinkedHashSet<>();
		this.environment = new HashMap<>();
		this.arguments.add("server");
		this.arguments.add("--json");
	}

	@Override
	public EsthreeMock.Builder volumes(List<String> volumes) {
		this.volumes.addAll(volumes);
		return this;
	}

	@Override
	public EsthreeMock.Builder volume(String volume) {
		this.volumes.add(volume);
		return this;
	}

	@Override
	public EsthreeMock.Builder temporaryVolume() {
		try {
			this.volumes.clear();
			this.temporary = Files.createTempDirectory("esthree-");
			return this;
		} catch (IOException exception) {
			throw new IllegalStateException("Failed to create temporary volume in EsthreeMock Builder", exception);
		}
	}

	@Override
	public EsthreeMock.Builder console(int port) {
		this.arguments.add("--console-address");
		this.arguments.add(":" + port);
		return this;
	}

	@Override
	public EsthreeMock.Builder disableConsole() {
		this.environment.put("MINIO_BROWSER", "off");
		return this;
	}

	@Override
	public EsthreeMock.Builder certificates(Path directory) {
		this.arguments.add("--certs-dir");
		this.arguments.add(directory.toAbsolutePath().toString());
		return this;
	}

	@Override
	public EsthreeMock.Builder ftps(int port, Path publicKey, Path privateKey) {
		this.arguments.add("--ftp=\"" + "address=:" + port + "\"");
		this.arguments.add("--ftp=\"" + "tls-public-cert=" + publicKey.toAbsolutePath() + "\"");
		this.arguments.add("--ftp=\"" + "tls-private-key=" + privateKey.toAbsolutePath() + "\"");
		return this;
	}

	@Override
	public EsthreeMock.Builder sftp(int port, Path privateKey) {
		this.arguments.add("--sftp=\"" + "address=:" + port + "\"");
		this.arguments.add("--sftp=\"" + "ssh-private-key=" + privateKey.toAbsolutePath() + "\"");
		return this;
	}

	@Override
	public EsthreeMock.Builder quiet() {
		this.arguments.add("--quiet");
		return this;
	}

	@Override
	public EsthreeMock.Builder anonymous() {
		this.arguments.add("--anonymous");
		return this;
	}

	@Override
	public EsthreeMock build() {
		Stream<String> volumes = (this.temporary != null) ? Stream.of(this.temporary.toAbsolutePath().toString()) : this.volumes.stream();
		List<String> arguments = Stream.concat(this.arguments.stream(), volumes).toList();
		return new DEsthreeMock(this.environment, arguments, this.temporary);
	}
}