package sirius.stellar.configuration;

import org.jspecify.annotations.Nullable;
import sirius.stellar.lifecycle.spi.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/// Implementation of [ConfigurationProvider] which loads all `.properties` files from the
/// filesystem relative to the working directory of the application.
///
/// Before loading, it will load any authored template files found on the module path.
/// Template configuration files are declared in the `META-INF/MANIFEST.MF` file of a given
/// JAR module, in the value of the `sirius.stellar.configuration: ` main manifest attribute.
///
/// The paths must not contain preceding or trailing slashes.
/// Multiple resource paths can be declared, separated with a semicolon (`;`) delimiter, e.g.:
/// ```
/// Manifest-Version: 1.0
/// sirius.stellar.configuration: META-INF/example.properties;META-INF/example2.properties
/// ```
///
/// @see Configuration
@Service.Provider
public final class PropertiesConfigurationProvider implements ConfigurationProvider {

	@Override
	public Map<String, String> get() throws IOException {
		Properties properties = new Properties();

		for (Module module : ModuleLayer.boot().modules()) {
			Manifest manifest = this.manifest(module);
			if (manifest == null) continue;

			String[] paths = manifest.getMainAttributes()
					.entrySet()
					.stream()
					.filter(entry -> entry.getKey().equals("sirius.stellar.configuration"))
					.findFirst()
					.map(Map.Entry::getValue)
					.map(String::valueOf)
					.map(names -> names.split(";"))
					.orElse(new String[0]);

			for (String value : paths) {
				Path path = Path.of(value);
				if (Files.exists(path)) continue;

				InputStream stream = module.getResourceAsStream(value);
				Files.copy(stream, path.getFileName());
			}
		}

		for (Path path : this.working()) {
			try (Reader reader = Files.newBufferedReader(path)) {
				properties.load(reader);
			}
		}

		return properties.entrySet()
				.stream()
				.collect(toMap(
					entry -> String.valueOf(entry.getKey()),
					entry -> String.valueOf(entry.getValue())
				));
	}

	/// Return a [Manifest] for the provided [Module] if present, or `null`.
	private @Nullable Manifest manifest(Module module) {
		try {
			InputStream stream = module.getResourceAsStream("META-INF/MANIFEST.MF");
			if (stream == null) return null;

			return new Manifest(stream);
		} catch (IOException exception) {
			return null;
		}
	}

	/// Return a list of files in the current working directory.
	private List<Path> working() throws IOException {
		Path working = Path.of("./");
		try (Stream<Path> stream = Files.list(working)
				.filter(Files::isRegularFile)
				.filter(path -> path.getFileName().toString().endsWith(".properties"))) {
			return stream.collect(toList());
		}
	}
}