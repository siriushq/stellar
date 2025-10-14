import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.esthree.server {

	requires org.jspecify;

	requires sirius.stellar.logging;
	requires io.avaje.jsonb;

	exports sirius.stellar.esthree.server;
}