package sirius.stellar.configuration.toml;

import io.avaje.spi.ServiceProvider;
import org.tomlj.Toml;
import org.tomlj.TomlArray;
import sirius.stellar.configuration.Configuration;
import sirius.stellar.configuration.ConfigurationProvider;
import sirius.stellar.configuration.file.FileConfigurationProvider;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

/// Implementation of [FileConfigurationProvider] for `.toml` files.
/// Uses the [org.tomlj] library for parsing.
///
/// ### Usage
/// Arrays parsed by this implementation are joined with semicolon delimiting,
/// (`x;y;z`), and can be read using e.g. [Configuration#propertyAs] with the
/// [String#split] method.
///
/// Table nesting with this implementation simply topologically exposes them
/// with period delimiting (`x.y.z=123`).
///
/// Types such as dates simply map to a string representation, e.g. ISO-8601,
/// which can then be parsed using e.g. [Instant#parse].
///
/// Intermediary tables are not supported.
///
/// @see FileConfigurationProvider
/// @see Configuration
@ServiceProvider
public final class TomlConfigurationProvider
	implements FileConfigurationProvider {

	@Override
	public Map<String, String> get(InputStream stream) throws IOException {
		try (stream) {
			return Toml.parse(stream)
					.dottedEntrySet()
					.stream()
					.collect(toMap(Map.Entry::getKey, entry -> value(entry.getValue())));
		}
	}

	@Override
	public Set<String> extensions() {
		return Set.of(".toml");
	}

	@Override
	public Class<? extends ConfigurationProvider> clazz() {
		return TomlConfigurationProvider.class;
	}

	/// Deserializes a TOML value from the provided [Object].
	private String value(Object object) {
		if (object instanceof TomlArray) {
			TomlArray array = (TomlArray) object;
			return array.toList().stream()
					.map(this::value)
					.collect(joining(";"));
		}
		return String.valueOf(object);
	}
}