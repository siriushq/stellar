// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.exception;

import java.io.Serial;
import java.nio.charset.CharacterCodingException;

/// Thrown to indicate an error when encoding/decoding a String value.
public class MessageStringCodingException extends MessagePackException {

	@Serial
	private static final long serialVersionUID = 3226472273623284189L;

	public MessageStringCodingException(String message, CharacterCodingException cause) {
		super(message, cause);
	}

	public MessageStringCodingException(CharacterCodingException cause) {
		super(cause);
	}

	@Override
	public CharacterCodingException getCause() {
		return (CharacterCodingException) super.getCause();
	}
}