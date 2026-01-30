import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.log4j2x {
	requires org.jspecify;

	requires sirius.stellar.logging;
	requires org.apache.logging.log4j;

	exports sirius.stellar.logging.dispatch.log4j2x;

	provides org.apache.logging.log4j.spi.LoggerContextFactory
		with sirius.stellar.logging.dispatch.log4j2x.Log4j2ContextFactory;

	provides org.apache.logging.log4j.spi.Provider
		with sirius.stellar.logging.dispatch.log4j2x.Log4j2Provider;
}