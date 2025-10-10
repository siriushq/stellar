/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sirius.stellar.serialization.msgpack.jsonb;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.Objects;

/// Implementation of [InputStream] wrapping a [Reader].
/// This is adapted from `org.apache.commons.io.input.ReaderInputStream` (to relieve the large dependency).
final class ReaderInputStream extends InputStream {

    private final Reader reader;
    private final CharsetEncoder charsetEncoder;
    private final CharBuffer encoderIn;
    private final ByteBuffer encoderOut;

    private CoderResult lastCoderResult;
    private boolean endOfInput;

    ReaderInputStream(final Reader reader) {
        this.reader = reader;
        this.charsetEncoder = Charset.defaultCharset().newEncoder();
        this.encoderIn = CharBuffer.allocate(8192);
        this.encoderIn.flip();
        this.encoderOut = ByteBuffer.allocate(128);
        this.encoderOut.flip();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    private void fillBuffer() throws IOException {
        if (endOfInput) return;
        if (lastCoderResult == null || lastCoderResult.isUnderflow()) {
            encoderIn.compact();
            final int position = encoderIn.position();
            // We don't use Reader#read(CharBuffer) here because it is more efficient
            // to write directly to the underlying char array (the default implementation
            // copies data to a temporary char array).
            final int c = reader.read(encoderIn.array(), position, encoderIn.remaining());
            if (c == -1) {
                endOfInput = true;
            } else {
                encoderIn.position(position + c);
            }
            encoderIn.flip();
        }
        encoderOut.compact();
        lastCoderResult = charsetEncoder.encode(encoderIn, encoderOut, endOfInput);
        if (endOfInput) {
            lastCoderResult = charsetEncoder.flush(encoderOut);
        }
        if (lastCoderResult.isError()) {
            lastCoderResult.throwException();
        }
        encoderOut.flip();
    }

	@Override
    public int read() throws IOException {
        while (!Thread.currentThread().isInterrupted()) {
            if (encoderOut.hasRemaining()) {
				return encoderOut.get() & 0xFF;
			}
            fillBuffer();
            if (endOfInput && !encoderOut.hasRemaining()) {
				return -1;
			}
        }
		throw new IOException("Current thread was interrupted (" + Thread.currentThread() + ")");
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(final byte[] array, int off, int len) throws IOException {
        Objects.requireNonNull(array, "array");
        if (len < 0 || off < 0 || off + len > array.length) {
            throw new IndexOutOfBoundsException("Array size=" + array.length + ", offset=" + off + ", length=" + len);
        }
        int read = 0;
        if (len == 0) {
            return 0; // Always return 0 if len == 0
        }
        while (len > 0) {
            if (encoderOut.hasRemaining()) { // Data from the last read not fully copied
                final int c = Math.min(encoderOut.remaining(), len);
                encoderOut.get(array, off, c);
                off += c;
                len -= c;
                read += c;
            } else if (endOfInput) { // Already reach EOF in the last read
                break;
            } else { // Read again
                fillBuffer();
            }
        }
        return read == 0 && endOfInput ? -1 : read;
    }
}