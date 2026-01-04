// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.buffer;

import static java.util.Objects.requireNonNull;

/**
 * MessageBufferInput adapter for byte arrays
 */
public class ArrayBufferInput
        implements MessageBufferInput
{
    private MessageBuffer buffer;
    private boolean isEmpty;

    public ArrayBufferInput(MessageBuffer buf)
    {
        this.buffer = buf;
        if (buf == null) {
            isEmpty = true;
        }
        else {
            isEmpty = false;
        }
    }

    public ArrayBufferInput(byte[] arr)
    {
        this(arr, 0, arr.length);
    }

    public ArrayBufferInput(byte[] arr, int offset, int length)
    {
        this(MessageBuffer.wrap(requireNonNull(arr, "input array is null"), offset, length));
    }

    /**
     * Reset buffer. This method returns the old buffer.
     *
     * @param buf new buffer. This can be null to make this input empty.
     * @return the old buffer.
     */
    public MessageBuffer reset(MessageBuffer buf)
    {
        MessageBuffer old = this.buffer;
        this.buffer = buf;
        if (buf == null) {
            isEmpty = true;
        }
        else {
            isEmpty = false;
        }
        return old;
    }

    public void reset(byte[] arr)
    {
        reset(MessageBuffer.wrap(requireNonNull(arr, "input array is null")));
    }

    public void reset(byte[] arr, int offset, int len)
    {
        reset(MessageBuffer.wrap(requireNonNull(arr, "input array is null"), offset, len));
    }

    @Override
    public MessageBuffer next()
    {
        if (isEmpty) {
            return null;
        }
        isEmpty = true;
        return buffer;
    }

    @Override
    public void close()
    {
        buffer = null;
        isEmpty = true;
    }
}
