import org.jspecify.annotations.NullMarked;
import sirius.stellar.logging.dispatch.log4j2x.Log4j2ContextFactory;

@NullMarked
module sirius.stellar.logging.log4j2x {

	requires org.jspecify;
	requires sirius.stellar.facility;
	requires sirius.stellar.logging;

	requires org.apache.logging.log4j;

	exports sirius.stellar.logging.dispatch.log4j2x;

	provides org.apache.logging.log4j.spi.LoggerContextFactory with Log4j2ContextFactory;
}