import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.serialization.msgpack {

	requires jdk.unsupported;
	requires org.jspecify;
	requires transitive io.avaje.jsonb;

	exports org.msgpack.core;
	exports org.msgpack.core.annotations;
	exports org.msgpack.core.buffer;

	exports org.msgpack.value;
	exports org.msgpack.value.impl;

	exports sirius.stellar.serialization.msgpack;

	provides io.avaje.jsonb.spi.JsonStreamFactory with sirius.stellar.serialization.msgpack.MsgpackAdapterFactory;
}