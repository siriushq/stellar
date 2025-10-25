// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.value.implementation;

import sirius.stellar.serialization.msgpack.exception.MessageTypeCastException;
import sirius.stellar.serialization.msgpack.value.ImmutableArrayValue;
import sirius.stellar.serialization.msgpack.value.ImmutableBinaryValue;
import sirius.stellar.serialization.msgpack.value.ImmutableBooleanValue;
import sirius.stellar.serialization.msgpack.value.ImmutableExtensionValue;
import sirius.stellar.serialization.msgpack.value.ImmutableFloatValue;
import sirius.stellar.serialization.msgpack.value.ImmutableIntegerValue;
import sirius.stellar.serialization.msgpack.value.ImmutableMapValue;
import sirius.stellar.serialization.msgpack.value.ImmutableNilValue;
import sirius.stellar.serialization.msgpack.value.ImmutableNumberValue;
import sirius.stellar.serialization.msgpack.value.ImmutableRawValue;
import sirius.stellar.serialization.msgpack.value.ImmutableStringValue;
import sirius.stellar.serialization.msgpack.value.ImmutableTimestampValue;
import sirius.stellar.serialization.msgpack.value.ImmutableValue;

abstract class DAbstractImmutableValue
        implements ImmutableValue
{
    @Override
    public boolean isNilValue()
    {
        return getValueType().isNilType();
    }

    @Override
    public boolean isBooleanValue()
	{
        return getValueType().isBooleanType();
    }

    @Override
    public boolean isNumberValue()
    {
        return getValueType().isNumberType();
    }

    @Override
    public boolean isIntegerValue()
    {
        return getValueType().isIntegerType();
    }

    @Override
    public boolean isFloatValue()
    {
        return getValueType().isFloatType();
    }

    @Override
    public boolean isRawValue()
    {
        return getValueType().isRawType();
    }

    @Override
    public boolean isBinaryValue()
    {
        return getValueType().isBinaryType();
    }

    @Override
    public boolean isStringValue()
    {
        return getValueType().isStringType();
    }

    @Override
    public boolean isArrayValue()
    {
        return getValueType().isArrayType();
    }

    @Override
    public boolean isMapValue()
    {
        return getValueType().isMapType();
    }

    @Override
    public boolean isExtensionValue()
    {
        return getValueType().isExtensionType();
    }

    @Override
    public boolean isTimestampValue()
    {
        return false;
    }

    @Override
    public ImmutableNilValue asNilValue()
    {
        throw new MessageTypeCastException();
    }

    @Override
    public ImmutableBooleanValue asBooleanValue()
    {
        throw new MessageTypeCastException();
    }

    @Override
    public ImmutableNumberValue asNumberValue()
    {
        throw new MessageTypeCastException();
    }

    @Override
    public ImmutableIntegerValue asIntegerValue()
    {
        throw new MessageTypeCastException();
    }

    @Override
    public ImmutableFloatValue asFloatValue()
    {
        throw new MessageTypeCastException();
    }

    @Override
    public ImmutableRawValue asRawValue()
    {
        throw new MessageTypeCastException();
    }

    @Override
    public ImmutableBinaryValue asBinaryValue()
    {
        throw new MessageTypeCastException();
    }

    @Override
    public ImmutableStringValue asStringValue()
    {
        throw new MessageTypeCastException();
    }

    @Override
    public ImmutableArrayValue asArrayValue()
    {
        throw new MessageTypeCastException();
    }

    @Override
    public ImmutableMapValue asMapValue()
    {
        throw new MessageTypeCastException();
    }

    @Override
    public ImmutableExtensionValue asExtensionValue()
    {
        throw new MessageTypeCastException();
    }

    @Override
    public ImmutableTimestampValue asTimestampValue()
    {
        throw new MessageTypeCastException();
    }
}
