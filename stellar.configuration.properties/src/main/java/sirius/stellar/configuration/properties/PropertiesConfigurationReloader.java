package sirius.stellar.configuration.properties;

import io.avaje.spi.ServiceProvider;
import sirius.stellar.configuration.Configuration;
import sirius.stellar.configuration.FileConfigurationReloader;

import java.util.Set;

/// Implementation of [FileConfigurationReloader] for `.properties` files.
///
/// @see FileConfigurationReloader
/// @see Configuration
@ServiceProvider
public final class PropertiesConfigurationReloader
	extends FileConfigurationReloader {

	@Override
	protected Set<String> extensions() {
		return Set.of(".properties");
	}
}