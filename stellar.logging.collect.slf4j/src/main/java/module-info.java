import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.collect.slf4j {

	requires org.jspecify;
	requires org.slf4j;

	requires sirius.stellar.logging;
	requires sirius.stellar.facility;

	exports sirius.stellar.logging.collect.slf4j;
}