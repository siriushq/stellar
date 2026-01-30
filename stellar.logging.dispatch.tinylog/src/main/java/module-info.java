import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.tinylog {
	requires org.jspecify;

	requires sirius.stellar.logging;
	requires org.tinylog.api;

	exports sirius.stellar.logging.dispatch.tinylog;

	provides org.tinylog.provider.LoggingProvider
		with sirius.stellar.logging.dispatch.tinylog.TinylogDispatcher;
}