// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.exception;

import java.io.Serial;

/// Thrown when the input MessagePack format is invalid.
public class MessageFormatException extends MessagePackException {

	@Serial
	private static final long serialVersionUID = 1386595118705185083L;

	public MessageFormatException(Throwable throwable) {
		super(throwable);
	}

	public MessageFormatException(String message) {
		super(message);
	}

	public MessageFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}