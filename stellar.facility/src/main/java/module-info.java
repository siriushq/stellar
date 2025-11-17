import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.facility {

	requires static org.jetbrains.annotations;
	requires org.jspecify;

	exports sirius.stellar.facility.concurrent;
	exports sirius.stellar.facility.annotation;
	exports sirius.stellar.facility.exception;
	exports sirius.stellar.facility.executor;
	exports sirius.stellar.facility.functional;
	exports sirius.stellar.facility.stream;
	exports sirius.stellar.facility.terminal;
	exports sirius.stellar.facility.tuple;
	exports sirius.stellar.facility;
}