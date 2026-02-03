package sirius.stellar.configuration.file;

import org.jspecify.annotations.Nullable;
import sirius.stellar.configuration.ConfigurationProvider;
import sirius.stellar.configuration.EnvironmentConfigurationProvider;
import sirius.stellar.configuration.SystemConfigurationProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;

/// Extension of [ConfigurationProvider] for making file-based configuration implementations.
///
/// ### Templates
/// In any given module on the module-path, a `META-INF/MANIFEST.MF` file can contain the
/// `sirius_stellar_configuration: ` main manifest attribute to specify what resources
/// (e.g. files in a given JAR) are to be extracted as configuration templates, if those
/// files do not already exist.
///
/// The paths must not contain preceding or trailing slashes.
/// Multiple resources can be declared, separated with a semicolon (`;`) delimiter, e.g.:
/// ```
/// Manifest-Version: 1.0
/// sirius_stellar_configuration: META-INF/example.properties;META-INF/example2.properties
/// ```
///
/// ### Loading
/// Configuration is loaded relative to the working directory of the application.
/// This can be adjusted by overriding the [#working] method, which by default uses the
/// implementation of [#extension] (which uses [#extensions]) to conveniently determine
/// what to load.
///
/// @since 1.0
public interface FileConfigurationProvider extends ConfigurationProvider {

	/// Obtain keys/value configuration data for the provided file [InputStream].
	/// Implementation of this method is responsible for closing the provided stream.
	///
	/// @see InputStream#close()
	/// @since 1.0
	Map<String, String> get(InputStream stream) throws IOException;

	/// A set of file extensions that can be recognized, in the current working directory,
	/// by this provider. Each string must begin with a period, e.g. `".properties"`.
	///
	/// @since 1.0
	Set<String> extensions();

	@Override
	default Map<String, String> get() {
		try {
			Map<String, String> all = new HashMap<>();

			for (Module module : ModuleLayer.boot().modules()) {
				Manifest manifest = manifest(module);
				if (manifest == null) continue;

				String paths = manifest.getMainAttributes()
						.entrySet()
						.stream()
						.filter(entry -> entry.getKey().equals("sirius_stellar_configuration"))
						.findFirst()
						.map(Map.Entry::getValue)
						.map(String::valueOf)
						.orElse(null);
				if (paths == null) continue;

				for (String value : paths.split(";")) {
					Path path = Path.of(value);
					if (Files.exists(path) || !this.extension(path.getFileName().toString())) continue;

					InputStream stream = module.getResourceAsStream(value);
					Files.copy(stream, path.getFileName());
				}
			}

			for (Path path : this.working()) {
				InputStream stream = Files.newInputStream(path);
				Map<String, String> result = this.get(stream);
				all.putAll(result);
			}

			return all;
		} catch (IOException exception) {
			return emptyMap();
		}
	}

	@Override
	default Set<Class<? extends ConfigurationProvider>> preceding() {
		return Set.of(EnvironmentConfigurationProvider.class, SystemConfigurationProvider.class);
	}

	/// Returns whether the provided [String] file name ends with one of the
	/// declared [#extensions()]. This method should not be overridden.
	///
	/// @since 1.0
	default boolean extension(String name) {
		int i = name.indexOf(".");
		if (i < 0) return false;

		return this.extensions().contains(name.substring(i));
	}

	/// Return a list of files to load from the current working directory.
	/// The default behavior loads all files that end with any [#extensions()].
	///
	/// @since 1.0
	default List<Path> working() throws IOException {
		Path working = Path.of("./");
		try (Stream<Path> stream = Files.list(working)
				.filter(Files::isRegularFile)
				.filter(path -> this.extension(path.getFileName().toString()))) {
			return stream.collect(toList());
		}
	}

	/// Return a [Manifest] for the provided [Module] if present, or `null`.
	private static @Nullable Manifest manifest(Module module) {
		try {
			InputStream stream = module.getResourceAsStream("META-INF/MANIFEST.MF");
			if (stream == null) return null;
			return new Manifest(stream);
		} catch (IOException exception) {
			return null;
		}
	}
}