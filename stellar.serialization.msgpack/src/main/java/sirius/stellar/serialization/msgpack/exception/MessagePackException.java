package sirius.stellar.serialization.msgpack.exception;

/**
 * A base class of all of the message pack exceptions.
 */
public class MessagePackException
        extends RuntimeException
{
    public MessagePackException()
    {
        super();
    }

    public MessagePackException(String message)
    {
        super(message);
    }

    public MessagePackException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public MessagePackException(Throwable cause)
    {
        super(cause);
    }

    public static UnsupportedOperationException UNSUPPORTED(String operationName)
    {
        return new UnsupportedOperationException(operationName);
    }

    public static final IllegalStateException UNREACHABLE = new IllegalStateException("Cannot reach here");
}
