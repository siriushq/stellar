import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.minlog {

	requires org.jspecify;
	requires sirius.stellar.facility;
	requires sirius.stellar.logging;

	requires com.esotericsoftware.minlog;

	exports sirius.stellar.logging.dispatch.minlog;

	provides sirius.stellar.logging.spi.LoggerExtension
		with sirius.stellar.logging.dispatch.minlog.MinlogDispatcher;
}