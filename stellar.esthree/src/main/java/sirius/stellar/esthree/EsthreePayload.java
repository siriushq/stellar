package sirius.stellar.esthree;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

/// Represents an S3 object, as returned by e.g. `GetObject`, or as provided
/// when invoking e.g. `PutObject`. This is fully immutable, and is just a
/// transparent data holder.
public interface EsthreePayload {

	/// The size of this payload (the S3 object) in bytes.
	long size();

	/// The MIME type of this payload (the S3 object).
	/// @see EsthreeMime enumeration of common types
	String type();

	/// The SHA256 checksum of this payload, if applicable.
	/// Returns an empty string (`""`) if not provided.
	String hash();

	/// Obtain the contents of this payload (the S3 object) as a stream.
	InputStream stream();

	/// Instantiate a payload populated with the provided data, and the
	/// provided known/pre-computed SHA256 checksum for this data.
	static EsthreePayload create(CharSequence type, long size, String hash, InputStream stream) {
		return new DEsthreePayload(type.toString(), size, hash, stream);
	}

	/// Instantiate a payload populated with the provided data, sans-checksum.
	/// An SHA256 checksum will be calculated automatically.
	/// @see #create(CharSequence, long, String, InputStream)
	static EsthreePayload create(CharSequence type, long size, InputStream stream) {
		return new DEsthreePayload(type.toString(), size, stream);
	}

	/// Instantiate a payload populated with the provided data, sans-checksum.
	/// An SHA256 checksum will be calculated automatically.
	/// @see #create(CharSequence, long, String, InputStream)
	static EsthreePayload create(CharSequence type, byte[] bytes) {
		return create(type, bytes.length, new ByteArrayInputStream(bytes));
	}

	/// Instantiate a payload populated with the provided data, sans-checksum.
	/// An SHA256 checksum will be calculated automatically.
	/// @see #create(CharSequence, long, String, InputStream).
	static EsthreePayload create(CharSequence type, ByteBuffer buffer) {
		byte[] bytes = new byte[buffer.remaining()];
		buffer.asReadOnlyBuffer().get(bytes);
		return create(type, bytes);
	}

	/// Instantiate a payload populated with the provided data, sans-checksum.
	/// An SHA256 checksum will be calculated automatically.
	/// @see #create(CharSequence, long, String, InputStream).
	static EsthreePayload create(CharSequence type, CharSequence characters) {
		return create(type, characters.toString().getBytes());
	}
}