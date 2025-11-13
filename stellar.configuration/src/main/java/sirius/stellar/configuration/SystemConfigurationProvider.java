package sirius.stellar.configuration;

import sirius.stellar.lifecycle.spi.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

/// Implementation of [ConfigurationProvider] returning [System#getProperties()].
/// @see Configuration
@Service.Provider
public final class SystemConfigurationProvider implements ConfigurationProvider {

	@Override
	public Map<String, String> get() {
		return System.getProperties()
				.entrySet()
				.stream()
				.collect(toMap(
					entry -> String.valueOf(entry.getKey()),
					entry -> String.valueOf(entry.getValue())
				));
	}

	@Override
	public List<Class<? extends ConfigurationProvider>> preceding() {
		return List.of(EnvironmentConfigurationProvider.class, PropertiesConfigurationProvider.class);
	}
}