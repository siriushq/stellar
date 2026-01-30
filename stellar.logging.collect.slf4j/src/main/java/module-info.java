import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.collect.slf4j {
	requires org.jspecify;

	requires sirius.stellar.logging;
	requires org.slf4j;

	exports sirius.stellar.logging.collect.slf4j;

	provides sirius.stellar.logging.spi.LoggerExtension
		with sirius.stellar.logging.collect.slf4j.Slf4jCollector;
}