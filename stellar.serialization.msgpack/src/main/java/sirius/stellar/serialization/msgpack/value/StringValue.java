package sirius.stellar.serialization.msgpack.value;

/**
 * Representation of MessagePack's String type.
 *
 * MessagePack's String type can represent a UTF-8 string at most 2<sup>64</sup>-1 bytes.
 *
 * Note that the value could include invalid byte sequences. {@code asString()} method throws {@code MessageTypeStringCodingException} if the value includes invalid byte sequence. {@code toJson()} method replaces an invalid byte sequence with <code>U+FFFD replacement character</code>.
 *
 * @see sirius.stellar.serialization.msgpack.value.RawValue
 */
public interface StringValue
        extends RawValue
{
}
