package sirius.stellar.serialization.msgpack.exception;

import java.nio.charset.CharacterCodingException;

/**
 * Thrown to indicate an error when encoding/decoding a String value
 */
public class MessageStringCodingException
        extends MessagePackException
{
    public MessageStringCodingException(String message, CharacterCodingException cause)
    {
        super(message, cause);
    }

    public MessageStringCodingException(CharacterCodingException cause)
    {
        super(cause);
    }

    @Override
    public CharacterCodingException getCause()
    {
        return (CharacterCodingException) super.getCause();
    }
}
