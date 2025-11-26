package sirius.stellar.configuration;

import sirius.stellar.lifecycle.spi.Service;
import sun.misc.Signal;

import static java.lang.System.*;

/// Implementation of [ConfigurationReloader] binding to `SIGHUP` via the
/// `sun.misc.Signal` and `sun.misc.SignalHandler` API.
///
/// @see Configuration
@Service.Provider
public final class SignalConfigurationReloader implements ConfigurationReloader {

	@Override
	public void wire() {
		if (getProperty("os.name").startsWith("Windows")) return;

		try {
			Signal sighup = new Signal("HUP");
			Signal.handle(sighup, signal -> this.reload());
		} catch (NoClassDefFoundError ignored) {
			err.println("sun.misc.Signal/SignalHandler unavailable, ignoring SIGHUP");
		} catch (IllegalArgumentException ignored) {
			err.println("failed to register sun.misc.Signal, ignoring SIGHUP");
		}
	}
}