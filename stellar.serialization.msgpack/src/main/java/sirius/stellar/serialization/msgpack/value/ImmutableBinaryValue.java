package sirius.stellar.serialization.msgpack.value;

/**
 * Immutable representation of MessagePack's Binary type.
 *
 * MessagePack's Binary type can represent a byte array at most 2<sup>64</sup>-1 bytes.
 *
 * @see sirius.stellar.serialization.msgpack.value.ImmutableRawValue
 */
public interface ImmutableBinaryValue
        extends BinaryValue, ImmutableRawValue
{
}
