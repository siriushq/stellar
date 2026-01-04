package sirius.stellar.configuration;

import io.avaje.spi.ServiceProvider;

import java.util.Set;

/// Implementation of [FileConfigurationReloader] for `.properties` files.
///
/// @see FileConfigurationReloader
/// @see Configuration
@ServiceProvider
public final class PropertiesConfigurationReloader extends FileConfigurationReloader {

	@Override
	protected Set<String> extensions() {
		return Set.of(".properties");
	}
}