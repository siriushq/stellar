package sirius.stellar.configuration.testing;

import io.avaje.spi.ServiceProvider;
import sirius.stellar.configuration.ConfigurationProvider;

import java.util.Map;

import static java.util.Collections.unmodifiableMap;
import static java.util.Map.copyOf;
import static sirius.stellar.configuration.testing.TestConfigurationState.configuration;

/// Implementation of [ConfigurationProvider] for serving values that
/// are populated in [TestConfigurationState].
///
/// @since 1.0
@ServiceProvider
public final class TestConfigurationProvider implements ConfigurationProvider {

	@Override
	public Map<String, String> get() {
		try {
			return copyOf(configuration);
		} finally {
			configuration.clear();
		}
	}

	@Override
	public Class<? extends ConfigurationProvider> clazz() {
		return TestConfigurationProvider.class;
	}
}