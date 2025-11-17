package sirius.stellar.platform;

import sirius.stellar.facility.Strings;
import sirius.stellar.facility.annotation.Internal;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Properties;

import static sirius.stellar.facility.functional.RethrowingConsumer.*;
import static sirius.stellar.facility.stream.TerminatingStream.*;
import static sirius.stellar.facility.tuple.Couple.*;

/**
 * Implementation of {@link Properties} which provides methods for loading the configuration
 * that {@link Platform} expects, including properties/env files, {@link System#getProperties()}
 * and {@link System#getenv()}, as well as populating default configurations (i.e., writing the
 * unwritten {@code .$template} files).
 * <p>
 * As {@link Properties} implements {@link java.util.Hashtable}, which implements {@link java.util.Map},
 * this is a fully serializable object and configuration can be transmitted across the network easily.
 */
@Internal
final class PlatformConfiguration extends Properties {

	@Serial
	private static final long serialVersionUID = 629487138782011311L;

	/**
	 * Loads all the configuration that {@link Platform} expects when initializing
	 * the instance for the current machine, creating a new {@link PlatformConfiguration}
	 * instance and returning it.
	 *
	 * @see Platform#Platform(String[], Instant)
	 */
	static PlatformConfiguration loadConfiguration() throws IOException {
		PlatformConfiguration configuration = new PlatformConfiguration();
		configuration.putAll(System.getenv());
		configuration.putAll(System.getProperties());

		for (Module module : ModuleLayer.boot().modules()) {
			InputStream stream = module.getResourceAsStream("META-INF/STELLAR/CONFIGURATION_TEMPLATES.LIST");
			if (stream == null) continue;

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
				reader.lines()
						.filter(file -> file.endsWith(".$template"))
						.map(line -> immutableCouple(line, Path.of(line).getFileName()))
						.filter(couple -> !Files.exists(couple.second()))
						.forEach(rethrowing(couple -> Files.copy(module.getResourceAsStream(couple.first()), couple.second())));
			}
		}

		for (Path path : terminalStream(Files.list(Path.of(Strings.EMPTY).toAbsolutePath()))
				.filter(Files::isRegularFile)
				.filter(entry -> entry.getFileName().endsWith(".properties") || entry.getFileName().endsWith(".env"))
				.toList()) {
			InputStream stream = Files.newInputStream(path);
			configuration.load(stream);
		}

		return configuration;
	}
}