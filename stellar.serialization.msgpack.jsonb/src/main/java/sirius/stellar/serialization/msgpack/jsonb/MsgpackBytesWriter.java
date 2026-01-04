package sirius.stellar.serialization.msgpack.jsonb;

import io.avaje.json.stream.BufferedJsonWriter;
import io.avaje.json.stream.BytesJsonWriter;
import sirius.stellar.serialization.msgpack.MessageBufferPacker;

import static sirius.stellar.serialization.msgpack.MessagePack.newDefaultBufferPacker;

/// Implementation of [BufferedJsonWriter] for MessagePack.
final class MsgpackBytesWriter extends MsgpackWriter implements BytesJsonWriter {

	MsgpackBytesWriter(boolean serializeNulls, boolean serializeEmpty) {
		super(newDefaultBufferPacker(), serializeNulls, serializeEmpty);
	}

	@Override
	public byte[] result() {
		MessageBufferPacker packer = (MessageBufferPacker) this.packer;
		return packer.toByteArray();
	}
}