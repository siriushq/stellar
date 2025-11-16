// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.exception;

import sirius.stellar.serialization.msgpack.MessageFormat;

import java.io.Serial;

/// Thrown when the input MessagePack format is invalid, specifically containing [MessageFormat#NEVER_USED].
public class MessageNeverUsedFormatException extends MessageFormatException {

	@Serial
	private static final long serialVersionUID = -7974722337083725453L;

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