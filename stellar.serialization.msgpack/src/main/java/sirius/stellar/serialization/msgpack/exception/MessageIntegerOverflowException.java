package sirius.stellar.serialization.msgpack.exception;

import java.math.BigInteger;

/**
 * This error is thrown when the user tries to read an integer value
 * using a smaller types. For example, calling MessageUnpacker.unpackInt() for an integer value
 * that is larger than Integer.MAX_VALUE will cause this exception.
 */
public class MessageIntegerOverflowException
        extends MessageTypeException
{
    private final BigInteger bigInteger;

    public MessageIntegerOverflowException(BigInteger bigInteger)
    {
        super();
        this.bigInteger = bigInteger;
    }

    public MessageIntegerOverflowException(long value)
    {
        this(BigInteger.valueOf(value));
    }

    public MessageIntegerOverflowException(String message, BigInteger bigInteger)
    {
        super(message);
        this.bigInteger = bigInteger;
    }

    public BigInteger getBigInteger()
    {
        return bigInteger;
    }

    @Override
    public String getMessage()
    {
        return bigInteger.toString();
    }
}
