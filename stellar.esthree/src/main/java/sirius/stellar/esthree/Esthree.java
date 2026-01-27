package sirius.stellar.esthree;

import io.avaje.http.client.HttpClient;

import java.net.URI;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static java.util.ServiceLoader.load;

/// Service client for accessing S3.
///
/// This can be created using the static [#builder()] method.
/// Relinquish acquired resources with [#release()] & [#close()].
///
/// It is recommended that one instance is shared across an application, as
/// the client is fully thread-safe (and virtual threads are used to support
/// multithreaded execution on JVM >21, or [CompletableFuture]-based methods
/// can be used if virtual threads are unavailable).
///
/// ```
/// Esthree esthree = Esthree.builder()
///         .region(US_EAST_1)               // either set region (for AWS)
///         .endpoint("https://s3.acme.com") // or set endpoint (if not)
///         .credentials(                    // authenticate (required!)
///             "AKIAIOSFODNN7EXAMPLE",
///             "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY")
///         .build();
///
/// esthree.buckets()
///         .forEach(bucket -> System.out.println(bucket.name()));
/// ```
/// @see sirius.stellar.esthree
public interface Esthree extends AutoCloseable {

	//#region buckets*
	/// Returns a [Stream] of [EsthreeBucket]s, which iterates pages when a
	/// terminal operation is executed. This will lazily load the listing
	/// recursively using AWS pagination.
	///
	/// A [Stream] is used to prevent extraneous requests being made, as a
	/// contract to define that the [EsthreeBucket]s should only
	/// be consumed once.
	///
	/// If such iterating view is unsuitable [Stream#collect] or `Stream#toList`
	/// (on Java >16) can be used to obtain a persistent view.
	///
	/// This is a paginated view. If `ContinuationToken` is continually returned
	/// by S3 with every request, it is very possible that the [Stream] will
	/// take forever (or, a long time) to completely iterate every page; S3 is
	/// considered a trusted host, but this could be used as an attack vector.
	///
	/// @throws EsthreeException if the request failed
	Stream<EsthreeBucket> buckets();

	/// [Future]-based variant of [#buckets()].
	/// @throws EsthreeException if the request failed
	Stream<CompletableFuture<EsthreeBucket>> bucketsFuture();

	/// [#buckets()], efficiently limiting results with the provided prefix string.
	/// @throws EsthreeException if the request failed
	Stream<EsthreeBucket> buckets(String prefix);

	/// [Future]-based variant of [#buckets(String)].
	/// @throws EsthreeException if the request failed
	Stream<CompletableFuture<EsthreeBucket>> bucketsFuture(String prefix);
	//#endregion

	//#region createBucket*
	/// Create a bucket with the provided name.
	/// @throws EsthreeException if the request failed
	void createBucket(String name);

	/// [Future] based variant of [#createBucket].
	/// @throws EsthreeException if the request failed
	CompletableFuture<Void> createBucketFuture(String name);
	//#endregion createBucket*

	//#region deleteBucket*
	/// Delete a bucket with the provided name.
	/// @throws EsthreeException if the request failed
	void deleteBucket(String name);

	/// [Future] based variant of [#deleteBucket].
	/// @throws EsthreeException if the request failed
	CompletableFuture<Void> deleteBucketFuture(String name);
	//#endregion

	//#region existsBucket
	/// Return whether a bucket with the provided name exists.
	/// @throws EsthreeException if the request failed
	boolean existsBucket(String name);

	/// [Future] based variant of [#existsBucket].
	/// @throws EsthreeException if the request failed
	CompletableFuture<Boolean> existsBucketFuture(String name);
	//#endregion

	//#region putPayload*
	/// Upload an object with the provided key, to a bucket provided by name.
	/// @throws EsthreeException if the request failed
	void putPayload(String bucket, String key, EsthreePayload payload);

	/// [Future] based variant of [#putPayload].
	/// @throws EsthreeException if the request failed
	CompletableFuture<Void> putPayloadFuture(String bucket, String key, EsthreePayload payload);
	//#endregion

	//#region getPayload*
	/// Stream an object with the provided key, from a bucket provided by name.
	/// @throws EsthreeException if the request failed
	EsthreePayload getPayload(String bucket, String key);

	/// [Future] based variant of [#getPayload].
	/// @throws EsthreeException if the request failed
	CompletableFuture<EsthreePayload> getPayloadFuture(String bucket, String key);
	//#endregion

	//#region existsPayload*
	/// Return whether an object with the provided key exists
	/// in a bucket provided by name.
	/// @throws EsthreeException if the request failed
	boolean existsPayload(String bucket, String key);

	/// [Future] based variant of [#existsPayload].
	/// @throws EsthreeException if the request failed
	CompletableFuture<Boolean> existsPayloadFuture(String bucket, String key);
	//#endregion

	//#region deletePayload*
	/// Delete an object with the provided key, from a bucket provided by name.
	/// @throws EsthreeException if the request failed
	void deletePayload(String bucket, String key);

	/// [Future] based variant of [#deletePayload].
	/// @throws EsthreeException if the request failed
	CompletableFuture<Void> deletePayloadFuture(String bucket, String key);
	//#endregion

	/// Access the underlying [HttpClient]. Most people should never use this method.
	HttpClient httpClient();

	/// Return the current aggregate metrics, collected for all requests.
	/// @see HttpClient.Metrics
	default HttpClient.Metrics metrics() {
		return this.httpClient().metrics();
	}

	/// Return (and optionally reset) the current aggregate metrics, collected for all requests.
	/// @see HttpClient.Metrics
	default HttpClient.Metrics metrics(boolean reset) {
		return this.httpClient().metrics(reset);
	}

	/// Release thread-local resources for only the current thread
	/// (the thread which is used to invoke/call this method).
	///
	/// This should be used when the client is no longer needed on the caller
	/// thread, or the memory should be freed, and it is more performant for
	/// re-initialization to occur later on, even if using the same thread.
	void release();

	/// Close this resource, relinquishing underlying resources.
	///
	/// This method cannot release thread-local resources created during the
	/// lifetime of the client, [#release()] should be used for that if the
	/// threads invoking this client are expected to be long-lived.
	///
	/// @see AutoCloseable#close()
	void close();

	/// Return a builder to construct [Esthree] instances with.
	static Builder builder() {
		try {
			ServiceLoader<Esthree.Builder> loader = load(Esthree.Builder.class);
			for (Esthree.Builder builder : loader) {
				if (builder instanceof DEsthreeBuilder) continue;
				return builder;
			}
			return new DEsthreeBuilder();
		} catch (Throwable throwable) {
			throw new IllegalStateException("Failed wiring alternate Esthree implementation", throwable);
		}
	}

	/// @see Esthree
	interface Builder {

		/// Configure the endpoint with which the client should communicate.
		///
		/// The default endpoint is `s3.<region>.amazonaws.com` and
		/// `true` virtual-hosted addressing style.
		///
		/// If using a third-party S3 implementation, disable virtual-hosted
		/// addressing unless supported.
		///
		/// If this is not specified, the client will attempt to identify the
		/// endpoint automatically using the `aws.endpointUrl` Java property
		/// and `AWS_ENDPOINT_URL` environment variable. If those are found,
		/// `false` virtual-hosted addressing becomes the default.
		///
		/// @see #region
		Builder endpoint(String endpoint, boolean virtual);

		/// @see #endpoint(String, boolean)
		default Builder endpoint(URI endpoint, boolean virtual) {
			return this.endpoint(endpoint.normalize().toString(), virtual);
		}

		/// Configure the region with which the client should communicate.
		///
		/// If this is not specified, the client will attempt to identify the
		/// endpoint automatically using the `aws.region` Java property
		/// and `AWS_REGION` environment variable.
		///
		/// Otherwise, a default of [EsthreeRegion#US_EAST_1] is chosen.
		Builder region(String region);

		/// @see #region(String)
		default Builder region(EsthreeRegion region) {
			return this.region(region.toString());
		}

		/// Configure the credentials with which the client should authenticate.
		///
		/// If this is not specified, the client will attempt to identify the
		/// endpoint automatically using `aws.accessKeyId/aws.secretAccessKey`
		/// Java properties and `AWS_ACCESS_KEY_ID/AWS_SECRET_ACCESS_KEY`
		/// environment variables.
		Builder credentials(String accessKey, String secretKey);

		/// Access the builder for the underlying [HttpClient], for any
		/// further configuration. Most people should never use this method.
		HttpClient.Builder httpClientBuilder();

		/// Build and return the client (which is an [AutoCloseable]).
		/// @throws IllegalStateException no credentials were provided
		Esthree build();
	}
}