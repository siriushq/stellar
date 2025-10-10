package sirius.stellar.serialization.msgpack.value;

/**
 * Representation of MessagePack's Extension type.
 *
 * MessagePack's Extension type can represent represents a tuple of type information and a byte array where type information is an
 * integer whose meaning is defined by applications.
 *
 * As the type information, applications can use 0 to 127 as the application-specific types. -1 to -128 is reserved for MessagePack's future extension.
 */
public interface ExtensionValue
        extends Value
{
    byte getType();

    byte[] getData();
}
