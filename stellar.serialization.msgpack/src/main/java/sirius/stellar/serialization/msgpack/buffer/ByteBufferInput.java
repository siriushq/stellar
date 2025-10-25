// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.buffer;

import java.nio.ByteBuffer;

import static java.util.Objects.*;

/**
 * {@link MessageBufferInput} adapter for {@link ByteBuffer}
 */
public class ByteBufferInput
        implements MessageBufferInput
{
    private ByteBuffer input;
    private boolean isRead = false;

    public ByteBufferInput(ByteBuffer input)
    {
        this.input = requireNonNull(input, "input ByteBuffer is null").slice();
    }

    /**
     * Reset buffer.
     *
     * @param input new buffer
     * @return the old buffer
     */
    public ByteBuffer reset(ByteBuffer input)
    {
        ByteBuffer old = this.input;
        this.input = requireNonNull(input, "input ByteBuffer is null").slice();
        isRead = false;
        return old;
    }

    @Override
    public MessageBuffer next()
    {
        if (isRead) {
            return null;
        }

        MessageBuffer b = MessageBuffer.wrap(input);
        isRead = true;
        return b;
    }

    @Override
    public void close()
    {
        // Nothing to do
    }
}
