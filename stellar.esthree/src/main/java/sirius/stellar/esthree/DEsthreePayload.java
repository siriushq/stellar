package sirius.stellar.esthree;

import java.io.InputStream;

/// Domain implementation of [EsthreePayload].
final class DEsthreePayload implements EsthreePayload {

	private final String type;
	private final long size;
	private final String hash;
	private final InputStream stream;

	DEsthreePayload(String type, long size, String hash, InputStream stream) {
		this.type = type;
		this.size = size;
		this.hash = hash;
		this.stream = stream;
	}

	DEsthreePayload(String type, long size, InputStream stream) {
		this(type, size, "", stream);
	}

	@Override
	public long size() {
		return this.size;
	}

	@Override
	public String type() {
		return this.type;
	}

	@Override
	public String hash() {
		return this.hash;
	}

	@Override
	public InputStream stream() {
		return this.stream;
	}
}