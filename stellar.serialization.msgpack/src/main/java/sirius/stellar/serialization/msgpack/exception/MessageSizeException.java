// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.exception;

import java.io.Serial;

/// Thrown to indicate too large message size (e.g, larger than 2^31-1).
public class MessageSizeException extends MessagePackException {

	@Serial
	private static final long serialVersionUID = 3484488351397983738L;

	private final long size;

	public MessageSizeException(long size) {
		super();
		this.size = size;
	}

	public MessageSizeException(String message, long size) {
		super(message);
		this.size = size;
	}

	public long size() {
		return size;
	}
}