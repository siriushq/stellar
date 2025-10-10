package sirius.stellar.serialization.msgpack.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import static java.util.Objects.*;

/**
 * {@link MessageBufferOutput} adapter for {@link WritableByteChannel}
 */
public class ChannelBufferOutput
        implements MessageBufferOutput
{
    private WritableByteChannel channel;
    private MessageBuffer buffer;

    public ChannelBufferOutput(WritableByteChannel channel)
    {
        this(channel, 8192);
    }

    public ChannelBufferOutput(WritableByteChannel channel, int bufferSize)
    {
        this.channel = requireNonNull(channel, "output channel is null");
        this.buffer = MessageBuffer.allocate(bufferSize);
    }

    /**
     * Reset channel. This method doesn't close the old channel.
     *
     * @param channel new channel
     * @return the old channel
     */
    public WritableByteChannel reset(WritableByteChannel channel)
            throws IOException
    {
        WritableByteChannel old = this.channel;
        this.channel = channel;
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
        ByteBuffer bb = buffer.sliceAsByteBuffer(0, length);
        while (bb.hasRemaining()) {
            channel.write(bb);
        }
    }

    @Override
    public void write(byte[] buffer, int offset, int length)
            throws IOException
    {
        ByteBuffer bb = ByteBuffer.wrap(buffer, offset, length);
        while (bb.hasRemaining()) {
            channel.write(bb);
        }
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
        channel.close();
    }

    @Override
    public void flush()
            throws IOException
    { }
}
