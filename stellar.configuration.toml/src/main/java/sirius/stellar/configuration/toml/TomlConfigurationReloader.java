package sirius.stellar.configuration.toml;

import io.avaje.spi.ServiceProvider;
import sirius.stellar.configuration.Configuration;
import sirius.stellar.configuration.FileConfigurationReloader;

import java.util.Set;

/// Implementation of [FileConfigurationReloader] for `.toml` files.
///
/// @see FileConfigurationReloader
/// @see Configuration
@ServiceProvider
public final class TomlConfigurationReloader extends FileConfigurationReloader {

	@Override
	protected Set<String> extensions() {
		return Set.of(".toml");
	}
}