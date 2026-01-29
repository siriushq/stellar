import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.jul {

	requires org.jspecify;
	requires sirius.stellar.facility;
	requires sirius.stellar.logging;

	requires java.logging;

	exports sirius.stellar.logging.dispatch.jul;

	provides sirius.stellar.logging.spi.LoggerExtension
		with sirius.stellar.logging.dispatch.jul.JulDispatcher;
}