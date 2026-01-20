import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.collect.csv {
	requires org.jspecify;
	requires sirius.stellar.logging;

	exports sirius.stellar.logging.collect.csv;
}