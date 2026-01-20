package sirius.stellar.logging.spi;

import sirius.stellar.logging.LoggerMessage;

/// Represents a dispatcher (producer) of logger messages.
/// Implementation can be provided as [LoggerExtension] service providers.
///
/// This is only used to wire any dispatchers that produce messages delegating
/// other logging APIs, that are not automatically service loaded by those given
/// logging facades.
///
/// @see LoggerExtension
/// @since 1.0
public interface LoggerDispatcher extends LoggerExtension {

	/// Run automatically when this dispatcher is discovered.
	/// This method invocation is not expected to be reversible.
	///
	/// Permanent (until application restart) changes e.g. mutating static state
	/// (fields, methods), modifying files, database changes, etc., are to be
	/// made from this method.
	///
	/// @since 1.0
	void wire() throws Throwable;

	/// Obtain a builder for logger messages, which can directly be used for
	/// dispatching with a fluent API. This is a convenience method.
	///
	/// @since 1.0
	static LoggerMessage.Builder message() {
		return LoggerMessage.builder();
	}
}