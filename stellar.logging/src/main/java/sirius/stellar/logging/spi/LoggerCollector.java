package sirius.stellar.logging.spi;

import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerMessage;

/// Represents a collector (consumer) of logger messages.
///
/// The minimum requirement for an implementation is to override [#collect].
/// Implementation can be provided as [LoggerExtension] service providers.
///
/// @see LoggerExtension
/// @since 1.0
public interface LoggerCollector extends LoggerExtension, AutoCloseable {

	/// Runs when a logger message is emitted.
	/// This method is invoked from a worker on a virtual thread.
	///
	/// @since 1.0
	void collect(LoggerMessage message);

	@Override
	default void wire() {
		Logger.collector(this);
	}

	/// Runs when this collector is closed.
	///
	/// Only unchecked exceptions may be thrown from this context, and collector
	/// implementations can be defined without overriding this method if they do
	/// not maintain any closeable resources.
	///
	/// @throws RuntimeException failure to clean up collector
	/// @since 1.0
	@Override
	default void close() {
		assert true;
	}
}