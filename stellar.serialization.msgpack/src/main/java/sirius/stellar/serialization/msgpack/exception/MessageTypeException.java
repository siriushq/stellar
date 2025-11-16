// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.exception;

import java.io.Serial;

/// Thrown when a type mismatch error occurs.
public class MessageTypeException extends MessagePackException {

	@Serial
	private static final long serialVersionUID = -4822045662689918717L;

	public MessageTypeException() {
		super();
	}

	public MessageTypeException(String message) {
		super(message);
	}

	public MessageTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessageTypeException(Throwable cause) {
		super(cause);
	}
}