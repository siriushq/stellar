package sirius.stellar.esthree;

import io.avaje.http.client.BodyContent;
import io.avaje.http.client.HttpClientRequest;

import java.io.InputStream;

/// Abstraction to sign requests using AWS Signature V4.
/// This can be created using the static [#create] method.
public interface EsthreeSigner extends AutoCloseable {

	/// Acquire the signer for the current thread.
	///
	/// If this method is not called before attempting to use the signer,
	/// operations can be expected to throw a [IllegalStateException].
	///
	/// @throws IllegalStateException failure in cryptographic primitive
	/// @see #close()
	EsthreeSigner acquire();

	/// Release any resources held by this signer for the current thread.
	///
	/// If this method is left uncalled, these resources will still be cleaned
	/// up automatically when the thread is destroyed.
	///
	/// This does not follow the same semantics as the `close` name suggests,
	/// another call to [#acquire()] can allow you to continue to reuse this
	/// signer instance.
	///
	/// @see #acquire()
	void close();

	/// Sign a request with a fully known body (i.e. String, byte[]).
	///
	/// Even if empty body content is required, `BodyContent.of(new byte[0])` should
	/// be provided to this method to calculate the SHA256 hash for the empty body.
	///
	/// @param method The HTTP method that will be used (e.g. GET, PUT).
	/// @throws IllegalStateException did not call [#acquire()]
	/// @see BodyContent
	void sign(String method, HttpClientRequest request, BodyContent body);

	/// Sign a request for streaming (chunked) payloads.
	///
	/// This returns a wrapped version of the provided stream which signs
	/// chunks as they are read.
	///
	/// @param method The HTTP method that will be used (e.g. GET, PUT).
	/// @throws IllegalStateException did not call [#acquire()]
	InputStream sign(String method, HttpClientRequest request, InputStream stream);

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
}