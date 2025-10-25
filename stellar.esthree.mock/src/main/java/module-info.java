import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.esthree.mock {

	requires io.avaje.jsonb;
	requires org.jspecify;

	requires sirius.stellar.logging;
	requires sirius.stellar.facility;

	exports sirius.stellar.esthree.mock;

	provides io.avaje.jsonb.spi.JsonbExtension with sirius.stellar.esthree.mock.MockJsonComponent;
}