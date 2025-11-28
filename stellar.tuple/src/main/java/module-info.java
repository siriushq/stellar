import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.tuple {

	requires org.jspecify;
	requires static org.jetbrains.annotations;

	requires sirius.stellar.facility;

	exports sirius.stellar.tuple;
}