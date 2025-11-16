// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.exception;

import java.io.Serial;

/// Thrown when a type mismatch error occurs.
public class MessageTypeCastException extends MessageTypeException {

	@Serial
	private static final long serialVersionUID = -1043968411483895461L;

	public MessageTypeCastException() {
		super();
	}

	public MessageTypeCastException(String message) {
		super(message);
	}

	public MessageTypeCastException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessageTypeCastException(Throwable cause) {
		super(cause);
	}
}