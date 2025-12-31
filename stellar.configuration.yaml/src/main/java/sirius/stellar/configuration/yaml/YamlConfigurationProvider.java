package sirius.stellar.configuration.yaml;

import io.avaje.spi.ServiceProvider;
import org.yaml.snakeyaml.Yaml;
import sirius.stellar.configuration.Configuration;
import sirius.stellar.configuration.ConfigurationProvider;
import sirius.stellar.configuration.FileConfigurationProvider;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.*;

/// Implementation of [FileConfigurationProvider] for `.yml`/`.yaml` files.
/// Uses the [org.yaml.snakeyaml] library for parsing.
///
/// ### Usage
/// Arrays parsed by this implementation are joined with semicolon delimiting,
/// (`x;y;z`), and can be read using e.g. [Configuration#propertyAs] with the
/// [String#split] method.
///
/// Object nesting with this implementation simply topologically exposes them
/// with period delimiting (`x.y.z=123`).
///
/// Types such as dates simply map to a string representation, e.g. ISO-8601,
/// which can then be parsed using e.g. [Instant#parse].
///
/// Intermediary objects are not supported.
///
/// @see FileConfigurationProvider
/// @see Configuration
@ServiceProvider
public final class YamlConfigurationProvider implements FileConfigurationProvider {

	private final Yaml yaml;

	public YamlConfigurationProvider() {
		this.yaml = new Yaml();
	}

	@Override
	public Map<String, String> get(InputStream stream) throws IOException {
		try (stream) {
			Map<String, Object> map = this.yaml.load(stream);

			Map<String, String> flat = new HashMap<>();
			flatten(map, "", flat);
			return flat;
		}
	}

	@Override
	public Set<String> extensions() {
		return Set.of(".yml", ".yaml");
	}

	@Override
	public Class<? extends ConfigurationProvider> clazz() {
		return YamlConfigurationProvider.class;
	}

	/// Recursively flattens a YAML tree to a topological, period delimited [Map].
	///
	/// @param tree the YAML tree to flatten
	/// @param prefix the current key path prefix
	/// @param result the [Map] to output to
	@SuppressWarnings("unchecked")
	private void flatten(Map<String, Object> tree, String prefix, Map<String, String> result) {
		for (Map.Entry<String, Object> entry : tree.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			String newKey = prefix.isEmpty() ? key : (prefix + "." + key);
			if (value instanceof Map) {
				Map<String, Object> nested = (Map<String, Object>) value;
				flatten(nested, newKey, result);
				return;
			}

			String newValue = value(value);
			result.put(newKey, newValue);
		}
	}

	/// Deserializes a YAML value from the provided [Object].
	private String value(Object object) {
		if (object instanceof Collection) {
			Collection<?> collection = (Collection<?>) object;
			return collection.stream()
					.map(this::value)
					.collect(joining(";"));
		}
		return String.valueOf(object);
	}
}