import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.esthree {
	requires java.xml;

	requires io.avaje.http.client;
	requires org.jspecify;

	exports sirius.stellar.esthree;

	uses sirius.stellar.esthree.Esthree.Builder;
}