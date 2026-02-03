import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.configuration.file {
	requires org.jspecify;

	requires sirius.stellar.configuration;

	exports sirius.stellar.configuration.file;
}