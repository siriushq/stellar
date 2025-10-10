package sirius.stellar.serialization.msgpack.value;

/// Immutable base interface of [ImmutableStringValue] and [ImmutableBinaryValue] interfaces. \
/// MessagePack's Raw type can represent a byte array at most 2<sup>64</sup>-1 bytes.
///
/// @see sirius.stellar.serialization.msgpack.value.ImmutableStringValue
/// @see sirius.stellar.serialization.msgpack.value.ImmutableBinaryValue
public interface ImmutableRawValue extends RawValue, ImmutableValue {}