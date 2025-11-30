package sirius.stellar.esthree.server;

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

/// Domain implementation of [EsthreeServer].
/// Use that interface for programmatic use (e.g. test suite).
///
/// This is directly runnable and is the entry-point `Main-Class` of the
/// [sirius.stellar.esthree.server] module.
public final class DEsthreeServer implements EsthreeServer {

	private final Path binary;
	private final Process process;
	private final ProcessHandle handle;
	private final Thread logger;
	private final Jsonb jsonb;

	private final @Nullable Path temporary;

	DEsthreeServer(Map<String, String> environment, List<String> arguments, @Nullable Path temporary) {
		this.temporary = temporary;
		this.jsonb = Jsonb.instance();

		EsthreeServerBinary binary = EsthreeServerBinary.create();
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
			throw new IllegalStateException("Failed to start EsthreeServer", exception);
		}
	}

	/// Dispatches logs to `stellar.logging`.
	private void log(BufferedReader reader) {
		try {
			String line;
			while ((line = reader.readLine()) != null && !this.logger.isInterrupted()) {
				DEsthreeServerMessage message = this.jsonb.type(DEsthreeServerMessage.class).fromJson(line);
				String thread = "EsthreeServer-" + this.process.pid();
				String name = "sirius.stellar.esthree.server";

				Logger.dispatch(message.time(), message.mappedLevel(), thread, name, message.message());
//				Map<String, Object> object = mapper.fromJsonObject(line);
//
//				String time = object.get("time").toString();
//				String message = object.get("message").toString();
//				String name = "sirius.stellar.esthree.server.DEsthreeServer";
//
//				LoggerLevel level = switch (object.get("level").toString()) {
//					case "ERROR", "FATAL" -> ERROR;
//					case "WARNING" -> WARNING;
//					case "INFO" -> INFORMATION;
//					case "DEBUG" -> DEBUGGING;
//					default -> INFORMATION;
//				};
//
//				object.remove("level");
//				object.remove("time");
//				object.remove("message");
//				message += object;
//
//				Logger.dispatch(Instant.parse(time), level, "EsthreeServer-" + this.process.pid(), name, message);
			}
			Logger.information("Successfully closed EsthreeServer with code {0,number,integer}.", this.process.waitFor());
		} catch (IOException | InterruptedException exception) {
			throw new IllegalStateException("Failed to read line when dispatching logging in EsthreeServer", exception);
		}
	}

	public static void main(String[] arguments) {
		// TODO - full implementation
		Logger.collector(Collector.console());
		Logger.severity(DEBUGGING.severity());

		EsthreeServer server = EsthreeServer.builder()
				.console(9090)
				.temporaryVolume()
				.build();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				server.close();
			} catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}));
	}

	@Override
	public void close() throws IOException, ExecutionException, InterruptedException {
		Logger.information("Attempting to close EsthreeServer...");

		this.process.getErrorStream().close();

		this.handle.destroy();
		this.handle.onExit().get();
		this.logger.interrupt();

		Files.deleteIfExists(this.binary);
		if (this.temporary == null) return;

		try (Stream<Path> walk = Files.walk(this.temporary)) {
			for (Path path : walk.toList()) Files.deleteIfExists(path);
		}
	}
}

final class DEsthreeServerBuilder implements EsthreeServer.Builder {

	private final Set<String> arguments;
	private final Set<String> volumes;
	private final Map<String, String> environment;

	private @Nullable Path temporary;

	DEsthreeServerBuilder() {
		this.arguments = new LinkedHashSet<>();
		this.volumes = new LinkedHashSet<>();
		this.environment = new HashMap<>();
		this.arguments.add("server");
		this.arguments.add("--json");
	}

	@Override
	public EsthreeServer.Builder volumes(List<String> volumes) {
		this.volumes.addAll(volumes);
		return this;
	}

	@Override
	public EsthreeServer.Builder volume(String volume) {
		this.volumes.add(volume);
		return this;
	}

	@Override
	public EsthreeServer.Builder temporaryVolume() {
		try {
			this.volumes.clear();
			this.temporary = Files.createTempDirectory("esthree-");
			return this;
		} catch (IOException exception) {
			throw new IllegalStateException("Failed to create temporary volume in EsthreeServer Builder", exception);
		}
	}

	@Override
	public EsthreeServer.Builder console(int port) {
		this.arguments.add("--console-address");
		this.arguments.add(":" + port);
		return this;
	}

	@Override
	public EsthreeServer.Builder disableConsole() {
		this.environment.put("MINIO_BROWSER", "off");
		return this;
	}

	@Override
	public EsthreeServer.Builder certificates(Path directory) {
		this.arguments.add("--certs-dir");
		this.arguments.add(directory.toAbsolutePath().toString());
		return this;
	}

	@Override
	public EsthreeServer.Builder ftps(int port, Path publicKey, Path privateKey) {
		this.arguments.add("--ftp=\"" + "address=:" + port + "\"");
		this.arguments.add("--ftp=\"" + "tls-public-cert=" + publicKey.toAbsolutePath() + "\"");
		this.arguments.add("--ftp=\"" + "tls-private-key=" + privateKey.toAbsolutePath() + "\"");
		return this;
	}

	@Override
	public EsthreeServer.Builder sftp(int port, Path privateKey) {
		this.arguments.add("--sftp=\"" + "address=:" + port + "\"");
		this.arguments.add("--sftp=\"" + "ssh-private-key=" + privateKey.toAbsolutePath() + "\"");
		return this;
	}

	@Override
	public EsthreeServer.Builder quiet() {
		this.arguments.add("--quiet");
		return this;
	}

	@Override
	public EsthreeServer.Builder anonymous() {
		this.arguments.add("--anonymous");
		return this;
	}

	@Override
	public EsthreeServer build() {
		Stream<String> volumes = (this.temporary != null) ? Stream.of(this.temporary.toAbsolutePath().toString()) : this.volumes.stream();
		List<String> arguments = Stream.concat(this.arguments.stream(), volumes).toList();
		return new DEsthreeServer(this.environment, arguments, this.temporary);
	}
}