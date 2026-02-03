package sirius.stellar.configuration.yaml;

import io.avaje.spi.ServiceProvider;
import sirius.stellar.configuration.Configuration;
import sirius.stellar.configuration.file.FileConfigurationReloader;

import java.util.Set;

/// Implementation of [FileConfigurationReloader] for `.yml`/`.yaml` files.
///
/// @see FileConfigurationReloader
/// @see Configuration
@ServiceProvider
public final class YamlConfigurationReloader extends FileConfigurationReloader {

	@Override
	protected Set<String> extensions() {
		return Set.of(".yml", ".yaml");
	}
}