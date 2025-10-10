package sirius.stellar.serialization.msgpack.exception;

import sirius.stellar.serialization.msgpack.MessageFormat;

/// Thrown when the input MessagePack format is invalid, specifically containing [MessageFormat#NEVER_USED].
public class MessageNeverUsedFormatException extends MessageFormatException {

	public MessageNeverUsedFormatException(Throwable throwable) {
		super(throwable);
	}

	public MessageNeverUsedFormatException(String message) {
		super(message);
	}

	public MessageNeverUsedFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}