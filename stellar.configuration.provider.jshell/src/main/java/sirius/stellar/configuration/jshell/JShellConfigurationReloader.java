package sirius.stellar.configuration.jshell;

import io.avaje.spi.ServiceProvider;
import sirius.stellar.configuration.Configuration;
import sirius.stellar.configuration.FileConfigurationReloader;

import java.util.Set;

/// Implementation of [FileConfigurationReloader] for `.jsh`/`.jshell` files.
///
/// @see FileConfigurationReloader
/// @see Configuration
@ServiceProvider
public final class JShellConfigurationReloader extends FileConfigurationReloader {

	@Override
	public Set<String> extensions() {
		return Set.of(".jsh", ".jshell");
	}
}