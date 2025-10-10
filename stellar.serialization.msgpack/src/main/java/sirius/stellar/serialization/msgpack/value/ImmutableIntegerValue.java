package sirius.stellar.serialization.msgpack.value;

/**
 * Immutable representation of MessagePack's Integer type.
 *
 * MessagePack's Integer type can represent from -2<sup>63</sup> to 2<sup>64</sup>-1.
 */
public interface ImmutableIntegerValue
        extends IntegerValue, ImmutableNumberValue
{
}
