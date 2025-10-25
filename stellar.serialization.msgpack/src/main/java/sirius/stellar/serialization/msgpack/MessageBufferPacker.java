// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack;

import sirius.stellar.serialization.msgpack.buffer.ArrayBufferOutput;
import sirius.stellar.serialization.msgpack.buffer.MessageBuffer;
import sirius.stellar.serialization.msgpack.buffer.MessageBufferOutput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/// MessagePacker that is useful to produce byte array output.
///
/// This class allocates a new buffer instead of resizing the buffer when data
/// doesn't fit in the initial capacity. This is faster than [ByteArrayOutputStream],
/// especially when the size of written bytes is large, because resizing a buffer
/// usually needs to copy contents of the buffer.
public class MessageBufferPacker extends MessagePacker {

	protected MessageBufferPacker(MessagePack.PackerConfig config) {
		this(new ArrayBufferOutput(config.bufferSize()), config);
	}

	protected MessageBufferPacker(ArrayBufferOutput output, MessagePack.PackerConfig config) {
		super(output, config);
	}

	@Override
	public MessageBufferOutput reset(MessageBufferOutput output) throws IOException {
		if (!(output instanceof ArrayBufferOutput)) throw new IllegalArgumentException("MessageBufferPacker accepts only ArrayBufferOutput");
		return super.reset(output);
	}

	@Override
	public void clear() {
		super.clear();
		getArrayBufferOut().clear();
	}

	private ArrayBufferOutput getArrayBufferOut() {
		return (ArrayBufferOutput) this.output;
	}

	/// Gets copy of the written data as a byte array.
	///
	/// If your application needs better performance and smaller memory consumption,
	/// you may prefer [#toMessageBuffer()] or [#toBufferList()] to avoid copying.
	public byte[] toByteArray() {
		try {
			flush();
		} catch (IOException ex) {
			// IOException must not happen because underlying ArrayBufferOutput never throws IOException
			throw new RuntimeException(ex);
		}
		return getArrayBufferOut().toByteArray();
	}

	/// Gets the written data as a [MessageBuffer].
	///
	/// Unlike [#toByteArray()], this method omits copy of the contents if size
	/// of the written data is smaller than a single buffer capacity.
	public MessageBuffer toMessageBuffer() {
		try {
			flush();
		} catch (IOException exception) {
			// IOException must not happen because underlying ArrayBufferOutput never throws IOException
			throw new RuntimeException(exception);
		}
		return getArrayBufferOut().toMessageBuffer();
	}

	/// Returns the written data as a list of [MessageBuffer].
	///
	/// Unlike [#toByteArray()] or [#toMessageBuffer()], this is the fastest
	/// method that doesn't copy contents in any cases.
	public List<MessageBuffer> toBufferList() {
		try {
			flush();
		} catch (IOException exception) {
			// IOException must not happen because underlying ArrayBufferOutput never throws IOException
			throw new RuntimeException(exception);
		}
		return getArrayBufferOut().toBufferList();
	}

	/// Returns the size of the buffer in use.
	public int getBufferSize() {
		return getArrayBufferOut().getSize();
	}
}