import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.facility {
	requires org.jspecify;

	requires static sirius.stellar.annotation;

	exports sirius.stellar.facility.exception;
	exports sirius.stellar.facility.executor;
	exports sirius.stellar.facility.functional;
	exports sirius.stellar.facility.stream;
	exports sirius.stellar.facility.terminal;
	exports sirius.stellar.facility;
}