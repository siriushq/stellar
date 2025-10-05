package sirius.stellar.esthree;

import io.avaje.http.client.BodyContent;
import io.avaje.http.client.HttpClientRequest;

import java.io.InputStream;

/// Abstraction to sign requests using AWS Signature V4.
/// This can be created using the static [#create] method.
public interface EsthreeSigner {

	/// Sign a request with a fully known body (i.e. String, byte[]).
	/// @see BodyContent
	void sign(HttpClientRequest request, BodyContent body);

	/// Sign a request for streaming (chunked) payloads.
	/// This returns a wrapped version of the provided stream, which signs chunks as they are read.
	InputStream sign(HttpClientRequest request, InputStream stream);

	/// Create an instance of [EsthreeSigner].
	///
	/// The provided region is usually ignored by non-AWS S3 implementations, so
	/// it is a good practice to use [Esthree.Region#US_EAST_1], unless using AWS.
	static EsthreeSigner create(String accessKey, String secretKey, String region) {
		return new DEsthreeSigner(accessKey, secretKey, region);
	}
}