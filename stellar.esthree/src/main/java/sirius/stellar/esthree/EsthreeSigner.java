package sirius.stellar.esthree;

import io.avaje.http.client.BodyContent;
import io.avaje.http.client.HttpClientRequest;

import java.io.InputStream;
import java.net.http.HttpRequest.BodyPublisher;

/// Abstraction to sign requests using AWS Signature V4.
/// This can be created using the static [#create] method.
public interface EsthreeSigner {

	/// Release any resources held by this signer for the current thread.
	///
	/// If this method is left uncalled, these resources will still be cleaned
	/// up automatically when the thread is destroyed.
	///
	/// This should be used when it is known that the signer will not be used
	/// for the rest of the lifetime of the given thread.
	void release();

	/// Sign a request with a fully known body (i.e. String, byte[]).
	///
	/// Even if empty body content is required, `BodyContent.of(new byte[0])` should
	/// be provided to this method to calculate the SHA256 hash for the empty body.
	///
	/// @param method The HTTP method that will be used (e.g. GET, PUT).
	/// @see BodyContent
	void sign(String method, HttpClientRequest request, BodyContent body);

	/// Sign a request for streaming (chunked) payloads.
	///
	/// This returns a wrapped version of the provided stream which signs
	/// chunks, using SHA256 checksums, as they are read, and attaches required
	/// headers to the provided request.
	///
	/// @param method The HTTP method that will be used (e.g. GET, PUT).
	/// @param size The known size of the provided stream.
	InputStream sign(String method, HttpClientRequest request, InputStream stream, long size);

	/// Sign a request with only a known checksum.
	/// @param method The HTTP method that will be used (e.g. GET, PUT).
	void sign(String method, HttpClientRequest request, Checksum checksum);

	/// Create an instance of [EsthreeSigner].
	///
	/// The provided region is usually ignored by non-AWS S3 implementations, so
	/// it is a good practice to use [Esthree.Region#US_EAST_1], unless using AWS.
	static EsthreeSigner create(String accessKey, String secretKey, String region) {
		return new DEsthreeSigner(accessKey, secretKey, region);
	}

	/// [Esthree.Region] based variant of [#create(String, String, String)].
	static EsthreeSigner create(String accessKey, String secretKey, Esthree.Region region) {
		return create(accessKey, secretKey, region.toString());
	}

	/// Represents a checksum attribute for any S3 requests.
	/// Known constants are statically exposed by string [#type], e.g [#SHA1].
	///
	/// To calculate a checksum, only SHA256 is supported with [#NONE] used in
	/// tandem with the automatic chunk-by-chunk stream signing.
	interface Checksum {
		String CRC32 = "crc32";
		String CRC32C = "crc32c";
		String SHA1 = "sha1";
		String SHA256 = "sha256";

		/// A representation of "no checksum". This is typically used in
		/// tandem with [#sign(String, HttpClientRequest, InputStream, long)].
		Checksum NONE = of("", "");

		/// Returns the type of this checksum, by S3 header name suffix.
		String type();

		/// Returns the raw checksum, or if this checksum [#type] is
		/// [#NONE], an empty string.
		String value();

		/// Provide a checksum of the provided type.
		static Checksum of(String type, String value) {
			return new DEsthreeSignerChecksum(type, value);
		}
	}
}