// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.exception;

import java.io.Serial;

/// Exception that indicates end of input.
public class MessageInsufficientBufferException extends MessagePackException {

	@Serial
	private static final long serialVersionUID = -4434520549029117922L;

	public MessageInsufficientBufferException() {
		super();
	}

	public MessageInsufficientBufferException(String message) {
		super(message);
	}

	public MessageInsufficientBufferException(Throwable cause) {
		super(cause);
	}

	public MessageInsufficientBufferException(String message, Throwable cause) {
		super(message, cause);
	}
}