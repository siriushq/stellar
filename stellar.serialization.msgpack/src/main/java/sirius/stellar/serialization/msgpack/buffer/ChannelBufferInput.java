package sirius.stellar.serialization.msgpack.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import static java.util.Objects.*;

/**
 * {@link MessageBufferInput} adapter for {@link ReadableByteChannel}
 */
public class ChannelBufferInput
        implements MessageBufferInput
{
    private ReadableByteChannel channel;
    private final MessageBuffer buffer;

    public ChannelBufferInput(ReadableByteChannel channel)
    {
        this(channel, 8192);
    }

    public ChannelBufferInput(ReadableByteChannel channel, int bufferSize)
    {
        this.channel = requireNonNull(channel, "input channel is null");
		if (bufferSize <= 0) throw new IllegalArgumentException("buffer size must be > 0: " + bufferSize);
		this.buffer = MessageBuffer.allocate(bufferSize);
	}

    /**
     * Reset channel. This method doesn't close the old resource.
     *
     * @param channel new channel
     * @return the old resource
     */
    public ReadableByteChannel reset(ReadableByteChannel channel)
            throws IOException
    {
        ReadableByteChannel old = this.channel;
        this.channel = channel;
        return old;
    }

    @Override
    public MessageBuffer next()
            throws IOException
    {
        ByteBuffer b = buffer.sliceAsByteBuffer();
        int ret = channel.read(b);
        if (ret == -1) {
            return null;
        }
        b.flip();
        return buffer.slice(0, b.limit());
    }

    @Override
    public void close()
            throws IOException
    {
        channel.close();
    }
}
