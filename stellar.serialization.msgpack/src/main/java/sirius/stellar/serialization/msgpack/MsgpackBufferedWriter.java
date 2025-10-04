package sirius.stellar.serialization.msgpack;

import io.avaje.json.stream.BufferedJsonWriter;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

/**
 * Implementation of {@link BufferedJsonWriter} for MessagePack.
 */
final class MsgpackBufferedWriter extends MsgpackWriter implements BufferedJsonWriter {

	MsgpackBufferedWriter(boolean serializeNulls, boolean serializeEmpty) {
		super(MessagePack.newDefaultBufferPacker(), serializeNulls, serializeEmpty);
	}

	@Override
	public String result() {
		MessageBufferPacker packer = (MessageBufferPacker) this.packer;
		return new String(packer.toByteArray());
	}
}