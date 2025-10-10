module sirius.stellar.serialization.msgpack {

	requires jdk.unsupported;
	requires org.jspecify;

	exports sirius.stellar.serialization.msgpack;
	exports sirius.stellar.serialization.msgpack.buffer;
	exports sirius.stellar.serialization.msgpack.value;
	exports sirius.stellar.serialization.msgpack.value.implementation;
	exports sirius.stellar.serialization.msgpack.exception;
}