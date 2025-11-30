import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.tuple {
	requires org.jspecify;
	requires sirius.stellar.facility;

	requires static sirius.stellar.annotation;

	exports sirius.stellar.tuple;
}