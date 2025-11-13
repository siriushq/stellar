package sirius.stellar.configuration;

import sirius.stellar.lifecycle.spi.Service;

import java.util.List;
import java.util.Map;

/// Implementation of [ConfigurationProvider] returning [System#getenv()].
/// @see Configuration
@Service.Provider
public final class EnvironmentConfigurationProvider implements ConfigurationProvider {

	@Override
	public Map<String, String> get() {
		return System.getenv();
	}

	@Override
	public List<Class<? extends ConfigurationProvider>> preceding() {
		return List.of(PropertiesConfigurationProvider.class);
	}
}