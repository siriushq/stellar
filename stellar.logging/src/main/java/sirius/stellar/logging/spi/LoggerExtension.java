package sirius.stellar.logging.spi;

/// SPI (Service Provider Interface) for any provider that aims to extend the
/// behavior of the logging system.
///
/// @see LoggerCollector
/// @see LoggerDispatcher
/// @since 1.0
public interface LoggerExtension {

	/// Run automatically when this provider is discovered.
	/// @since 1.0
	void wire() throws Throwable;
}