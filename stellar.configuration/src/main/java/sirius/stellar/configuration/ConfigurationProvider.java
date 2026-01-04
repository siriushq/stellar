package sirius.stellar.configuration;

import io.avaje.spi.Service;

import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;

/// Represents a source of key-value configuration data to be loaded by
/// [Configuration] on the first attempt to access any configuration keys.
///
/// These providers can declare preceding providers ("dependencies") that are
/// to be loaded before themselves, using [#preceding()].
///
/// @since 1.0
@Service
public interface ConfigurationProvider {

	/// Obtain all keys/value configuration data.
	/// @since 1.0
	Map<String, String> get() throws Throwable;

	/// Return the type of this configuration provider, used for comparison
	/// with existing statically provided [#preceding()] providers.
	///
	/// To implement this, just `return MyConfigurationProvider.class`, but it
	/// is entirely optional and the default [Object#getClass] call is perfectly
	/// sufficient to perform the same lookup.
	///
	/// @since 1.0
	default Class<? extends ConfigurationProvider> clazz() {
		return this.getClass();
	}

	/// A set of providers that this one requires load before itself.
	///
	/// Cyclic dependencies are resolved non-deterministically.
	/// Given `X <-> Y`, `X` can load before or after `Y` with no guarantees.
	///
	/// @since 1.0
	default Set<Class<? extends ConfigurationProvider>> preceding() {
		return emptySet();
	}
}