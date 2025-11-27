package sirius.stellar.configuration;

import io.avaje.spi.ServiceProvider;

import java.util.Map;

/// Implementation of [ConfigurationProvider] returning [System#getenv()].
/// @see Configuration
@ServiceProvider
public final class EnvironmentConfigurationProvider implements ConfigurationProvider {

	@Override
	public Map<String, String> get() {
		return System.getenv();
	}
}