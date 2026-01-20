import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.collect.json {
	requires org.jspecify;
	requires sirius.stellar.logging;

	exports sirius.stellar.logging.collect.json;
}