import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.log4j {

	requires org.jspecify;
	requires sirius.stellar.facility;
	requires sirius.stellar.logging;

	exports org.apache.log4j;
	exports org.apache.log4j.spi;
}