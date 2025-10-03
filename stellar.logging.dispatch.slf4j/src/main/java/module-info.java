import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.slf4j {

	requires org.jspecify;
	requires sirius.stellar.facility;
	requires sirius.stellar.logging;

	requires org.slf4j;

	exports sirius.stellar.logging.dispatch.slf4j;

	provides org.slf4j.spi.SLF4JServiceProvider with sirius.stellar.logging.dispatch.slf4j.Slf4jServiceProvider;
}