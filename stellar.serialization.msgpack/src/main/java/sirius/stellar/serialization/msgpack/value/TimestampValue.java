// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.value;

import java.time.Instant;

/**
 * Value representation of MessagePack's Timestamp type.
 */
public interface TimestampValue
        extends ExtensionValue
{
    long getEpochSecond();

    int getNano();

    long toEpochMillis();

    Instant toInstant();
}
