package sirius.stellar.configuration;

import io.avaje.spi.Service;

/// Represents a configuration reload dispatcher, which is wired by [Configuration] on
/// the first attempt to access any configuration keys.
///
/// @since 1.0
@Service
public interface ConfigurationReloader {

	/// Run when a reload dispatcher is wired.
	/// This method invocation is not expected to be reversible.
	///
	/// @since 1.0
	void wire() throws Throwable;

	/// Triggers a configuration reload, to be called by [#wire()] implementations.
	/// This method should never be overridden.
	///
	/// @since 1.0
	default void reload() {
		Configuration.reload();
	}
}