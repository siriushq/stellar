package sirius.stellar.serialization.msgpack.value;

/**
 * Immutable declaration of {@link Value} interface.
 */
public interface ImmutableValue
        extends Value
{
    @Override
    public ImmutableNilValue asNilValue();

    @Override
    public ImmutableBooleanValue asBooleanValue();

    @Override
    public ImmutableIntegerValue asIntegerValue();

    @Override
    public ImmutableFloatValue asFloatValue();

    @Override
    public ImmutableArrayValue asArrayValue();

    @Override
    public ImmutableMapValue asMapValue();

    @Override
    public ImmutableRawValue asRawValue();

    @Override
    public ImmutableBinaryValue asBinaryValue();

    @Override
    public ImmutableStringValue asStringValue();

    @Override
    public ImmutableTimestampValue asTimestampValue();
}
