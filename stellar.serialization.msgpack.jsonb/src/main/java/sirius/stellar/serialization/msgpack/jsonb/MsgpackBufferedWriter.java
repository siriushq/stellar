package sirius.stellar.serialization.msgpack.jsonb;

import io.avaje.json.stream.BufferedJsonWriter;
import sirius.stellar.serialization.msgpack.MessageBufferPacker;

import static sirius.stellar.serialization.msgpack.MessagePack.*;

/// Implementation of [BufferedJsonWriter] for MessagePack.
final class MsgpackBufferedWriter extends MsgpackWriter implements BufferedJsonWriter {

	MsgpackBufferedWriter(boolean serializeNulls, boolean serializeEmpty) {
		super(newDefaultBufferPacker(), serializeNulls, serializeEmpty);
	}

	@Override
	public String result() {
		MessageBufferPacker packer = (MessageBufferPacker) this.packer;
		return new String(packer.toByteArray());
	}
}