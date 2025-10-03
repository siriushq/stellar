import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.kwik {

	requires org.jspecify;
	requires sirius.stellar.facility;
	requires sirius.stellar.logging;

	requires tech.kwik.core;

	exports sirius.stellar.logging.dispatch.kwik;
}