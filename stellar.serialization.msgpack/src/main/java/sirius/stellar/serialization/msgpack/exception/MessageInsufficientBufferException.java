// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.exception;

/// Exception that indicates end of input.
public class MessageInsufficientBufferException extends MessagePackException {

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