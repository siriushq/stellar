// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.buffer;

import java.io.IOException;
import java.io.OutputStream;

import static java.util.Objects.*;

/**
 * MessageBufferOutput adapter for {@link OutputStream}.
 */
public class OutputStreamBufferOutput
        implements MessageBufferOutput
{
    private OutputStream out;
    private MessageBuffer buffer;

    public OutputStreamBufferOutput(OutputStream out)
    {
        this(out, 8192);
    }

    public OutputStreamBufferOutput(OutputStream out, int bufferSize)
    {
        this.out = requireNonNull(out, "output is null");
        this.buffer = MessageBuffer.allocate(bufferSize);
    }

    /**
     * Reset Stream. This method doesn't close the old stream.
     *
     * @param out new stream
     * @return the old stream
     */
    public OutputStream reset(OutputStream out)
            throws IOException
    {
        OutputStream old = this.out;
        this.out = out;
        return old;
    }

    @Override
    public MessageBuffer next(int minimumSize)
            throws IOException
    {
        if (buffer.size() < minimumSize) {
            buffer = MessageBuffer.allocate(minimumSize);
        }
        return buffer;
    }

    @Override
    public void writeBuffer(int length)
            throws IOException
    {
        write(buffer.array(), buffer.arrayOffset(), length);
    }

    @Override
    public void write(byte[] buffer, int offset, int length)
            throws IOException
    {
        out.write(buffer, offset, length);
    }

    @Override
    public void add(byte[] buffer, int offset, int length)
            throws IOException
    {
        write(buffer, offset, length);
    }

    @Override
    public void close()
            throws IOException
    {
        out.close();
    }

    @Override
    public void flush()
            throws IOException
    {
        out.flush();
    }
}
