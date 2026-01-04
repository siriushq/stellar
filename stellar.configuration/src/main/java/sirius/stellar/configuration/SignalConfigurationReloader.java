package sirius.stellar.configuration;

import io.avaje.spi.ServiceProvider;
import sun.misc.Signal;

import static java.lang.System.err;
import static java.lang.System.getProperty;

/// Implementation of [ConfigurationReloader] binding to `SIGHUP` via the
/// `sun.misc.Signal` and `sun.misc.SignalHandler` API.
///
/// @see Configuration
@ServiceProvider
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