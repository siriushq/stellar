package sirius.stellar.configuration;

import io.avaje.spi.ServiceProvider;

import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.*;

/// Implementation of [ConfigurationProvider] returning [System#getProperties()].
/// @see Configuration
@ServiceProvider
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
	public Set<Class<? extends ConfigurationProvider>> preceding() {
		return Set.of(EnvironmentConfigurationProvider.class);
	}
}