// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.facility.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

/// Implementation of [OutputStream] wrapping a [Writer].
/// This is adapted from `org.apache.commons.io.input.WriterOutputStream`.
public final class WriterOutputStream extends OutputStream {

    private final Writer writer;
    private final CharsetDecoder decoder;

    private final ByteBuffer decoderIn = ByteBuffer.allocate(128);
    private final CharBuffer decoderOut;

    public WriterOutputStream(Writer writer) {
        this.writer = writer;
        this.decoder = Charset.defaultCharset().newDecoder();
        this.decoderOut = CharBuffer.allocate(8192);
    }

    @Override
    public void close() throws IOException {
        processInput(true);
        flushOutput();
        writer.close();
    }

    @Override
    public void flush() throws IOException {
        flushOutput();
        writer.flush();
    }

    private void flushOutput() throws IOException {
		if (decoderOut.position() <= 0) return;
		writer.write(decoderOut.array(), 0, decoderOut.position());
		decoderOut.rewind();
	}

    private void processInput(final boolean endOfInput) throws IOException {
        // Prepare decoderIn for reading
        decoderIn.flip();
        CoderResult coderResult;
        while (true) {
            coderResult = decoder.decode(decoderIn, decoderOut, endOfInput);
            if (coderResult.isOverflow()) {
                flushOutput();
            } else if (coderResult.isUnderflow()) {
                break;
            } else {
                // The decoder is configured to replace malformed input and unmappable characters,
                // so we should not get here.
                throw new IOException("Unexpected coder result");
            }
        }
        // Discard the bytes that have been read
        decoderIn.compact();
    }

	@Override
    public void write(final byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(final byte[] b, int off, int len) throws IOException {
        while (len > 0) {
            final int c = Math.min(len, decoderIn.remaining());
            decoderIn.put(b, off, c);
            processInput(false);
            len -= c;
            off += c;
        }
    }

    @Override
    public void write(final int b) throws IOException {
        write(new byte[] { (byte) b }, 0, 1);
    }
}