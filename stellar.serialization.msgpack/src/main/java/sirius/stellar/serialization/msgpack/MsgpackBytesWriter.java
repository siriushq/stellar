package sirius.stellar.serialization.msgpack;

import io.avaje.json.stream.BufferedJsonWriter;
import io.avaje.json.stream.BytesJsonWriter;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

/**
 * Implementation of {@link BufferedJsonWriter} for MessagePack.
 */
final class MsgpackBytesWriter extends MsgpackWriter implements BytesJsonWriter {

	MsgpackBytesWriter(boolean serializeNulls, boolean serializeEmpty) {
		super(MessagePack.newDefaultBufferPacker(), serializeNulls, serializeEmpty);
	}

	@Override
	public byte[] result() {
		MessageBufferPacker packer = (MessageBufferPacker) this.packer;
		return packer.toByteArray();
	}
}