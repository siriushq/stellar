// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.exception;

/// Thrown when the input MessagePack format is invalid.
public class MessageFormatException extends MessagePackException {

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