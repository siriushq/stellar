import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.serialization.msgpack.jsonb {

	requires transitive io.avaje.jsonb;
	requires org.jspecify;

	requires sirius.stellar.facility;
	requires sirius.stellar.serialization.msgpack;

	exports sirius.stellar.serialization.msgpack.jsonb;

	provides io.avaje.jsonb.spi.JsonbExtension with sirius.stellar.serialization.msgpack.jsonb.MsgpackAdapterFactory;
}