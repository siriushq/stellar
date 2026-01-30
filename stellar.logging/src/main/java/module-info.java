import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging {
	requires org.jspecify;
	requires static sirius.stellar.annotation;

	exports sirius.stellar.logging.supplier;
	exports sirius.stellar.logging.format;
	exports sirius.stellar.logging.spi;
	exports sirius.stellar.logging;

	uses sirius.stellar.logging.format.LoggerFormatter;
	uses sirius.stellar.logging.concurrent.LoggerScheduler;
	uses sirius.stellar.logging.spi.LoggerExtension;
}