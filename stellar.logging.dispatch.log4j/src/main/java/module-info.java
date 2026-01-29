import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.log4j {
	requires org.jspecify;
	requires sirius.stellar.logging;

	requires ch.qos.reload4j;

	provides sirius.stellar.logging.spi.LoggerExtension
		with sirius.stellar.logging.dispatch.log4j.Log4jDispatcher;
}