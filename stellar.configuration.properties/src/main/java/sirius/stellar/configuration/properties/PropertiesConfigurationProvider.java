package sirius.stellar.configuration.properties;

import io.avaje.spi.ServiceProvider;
import sirius.stellar.configuration.Configuration;
import sirius.stellar.configuration.ConfigurationProvider;
import sirius.stellar.configuration.FileConfigurationProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

/// Implementation of [FileConfigurationProvider] for `.properties` files.
///
/// @see FileConfigurationProvider
/// @see Configuration
@ServiceProvider
public final class PropertiesConfigurationProvider
	implements FileConfigurationProvider {

	@Override
	public Map<String, String> get(InputStream stream) throws IOException {
		try (stream) {
			Properties properties = new Properties();
			properties.load(stream);

			return properties.entrySet()
				.stream()
				.collect(toMap(
					entry -> String.valueOf(entry.getKey()),
					entry -> String.valueOf(entry.getValue())
				));
		}
	}

	@Override
	public Set<String> extensions() {
		return Set.of(".properties");
	}

	@Override
	public Class<? extends ConfigurationProvider> clazz() {
		return PropertiesConfigurationProvider.class;
	}
}