// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.exception;

import sirius.stellar.serialization.msgpack.MessageUnpacker;

import java.io.Serial;
import java.math.BigInteger;

/// This error is thrown when the caller tries to read a numeric value
/// using a smaller numeric type.
///
/// For example, calling [MessageUnpacker#unpackInt()] for a value
/// that is larger than [Integer#MAX_VALUE] will cause this exception.
public class MessageIntegerOverflowException extends MessageTypeException {

	@Serial
	private static final long serialVersionUID = 5950422114695899642L;

	private final BigInteger number;

	public MessageIntegerOverflowException(BigInteger number) {
		super();
		this.number = number;
	}

	public MessageIntegerOverflowException(long value) {
		this(BigInteger.valueOf(value));
	}

	public MessageIntegerOverflowException(String message, BigInteger number) {
		super(message);
		this.number = number;
	}

	/// Get the numeric value which caused the overflow as a [BigInteger].
	public BigInteger number() {
		return this.number;
	}

	@Override
	public String getMessage() {
		return this.number.toString();
	}
}