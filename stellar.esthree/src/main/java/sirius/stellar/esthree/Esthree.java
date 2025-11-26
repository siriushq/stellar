package sirius.stellar.esthree;

import io.avaje.http.client.HttpClient;

import java.net.URI;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

/// Service client for accessing S3. \
/// This can be created using the static [#builder()] method, and is [AutoCloseable].
///
/// It is recommended that one instance is shared across an application, as the client is fully
/// thread-safe (and virtual threads are used to support multithreaded execution on JVM >21, or
/// [CompletableFuture]-based methods can be used if virtual threads are unavailable).
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

	/// Returns a [Stream] of [Bucket]s, which iterates pages when a terminal operation is executed.
	/// This will lazily load the listing recursively using AWS pagination.
	///
	/// A [Stream] is used to prevent extraneous requests being made, as a contract to define that
	/// the [Bucket]s should only be consumed once. If such iterating view is unsuitable,
	/// [Stream#collect] or `Stream#toList` (Java >16) can be used to obtain a persistent view.
	///
	/// This is a paginated view. If `ContinuationToken` is continually returned by S3 with every
	/// request, it is very possible that the [Stream] will take a long time to completely iterate
	/// every page; S3 is considered a trusted host, but this could be used as an attack vector.
	///
	/// @throws EsthreeException if the request failed
	Stream<Bucket> buckets();

	/// [Future]-based variant of [#buckets()].
	/// @throws EsthreeException if the request failed
	Stream<CompletableFuture<Bucket>> bucketsFuture();

	/// [#buckets()], efficiently limiting results with the provided prefix string.
	/// @throws EsthreeException if the request failed
	Stream<Bucket> buckets(String prefix);

	/// [Future]-based variant of [#buckets(String)].
	/// @throws EsthreeException if the request failed
	Stream<CompletableFuture<Bucket>> bucketsFuture(String prefix);

	/// Create a bucket with the provided name.
	/// @throws EsthreeException if the request failed
	void createBucket(String name);

	/// [Future] based variant of [#createBucket(String)].
	/// @throws EsthreeException if the request failed
	CompletableFuture<Void> createBucketFuture(String name);

	/// Delete a bucket with the provided name.
	/// @throws EsthreeException if the request failed
	void deleteBucket(String name);

	/// [Future] based variant of [#deleteBucket(String)].
	/// @throws EsthreeException if the request failed
	CompletableFuture<Void> deleteBucketFuture(String name);

	/// Return whether a bucket with the provided name exists.
	/// @throws EsthreeException if the request failed
	boolean existsBucket(String name);

	/// [Future] based variant of [#existsBucket(String)].
	/// @throws EsthreeException if the request failed
	CompletableFuture<Boolean> existsBucketFuture(String name);

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

	/// Return a builder to construct [Esthree] instances with.
	static Builder builder() {
		return new DEsthreeBuilder();
	}

	/// @see Esthree
	interface Builder {

		/// Configure the endpoint with which the client should communicate. \
		/// The default endpoint is `s3.<region>.amazonaws.com` and `true` virtual-hosted addressing style. \
		/// If using a third-party S3 implementation, disable virtual-hosted addressing unless supported.
		///
		/// If this is not specified, the client will attempt to identify the endpoint automatically
		/// using the `aws.endpointUrl` Java property and `AWS_ENDPOINT_URL` environment variable.
		/// If those are set, `false` virtual-hosted addressing becomes the default.
		///
		/// @see #region
		Builder endpoint(String endpoint, boolean virtual);

		/// @see #endpoint(String, boolean)
		default Builder endpoint(URI endpoint, boolean virtual) {
			return this.endpoint(endpoint.normalize().toString(), virtual);
		}

		/// Configure the region with which the client should communicate.
		///
		/// If this is not specified, the client will attempt to identify the endpoint automatically
		/// using the `aws.region` Java property and `AWS_REGION` environment variable.
		///
		/// Otherwise, a default of {@link Region#US_EAST_1} is chosen.
		Builder region(String region);

		/// @see #region(String)
		default Builder region(Region region) {
			return this.region(region.toString());
		}

		/// Configure the credentials with which the client should authenticate.
		///
		/// If this is not specified, the client will attempt to identify the endpoint automatically using the
		/// `aws.accessKeyId/aws.secretAccessKey` Java properties, and `AWS_ACCESS_KEY_ID/AWS_SECRET_ACCESS_KEY`
		/// environment variables.
		Builder credentials(String accessKey, String secretKey);

		/// Access the builder for the underlying [HttpClient], for any
		/// further configuration. Most people should never use this method.
		HttpClient.Builder httpClientBuilder();

		/// Build and return the client (which is an [AutoCloseable]).
		/// @throws IllegalStateException Method fails if no credentials were provided.
		Esthree build();
	}

	/// Enumerator for valid AWS regions that can be provided to [Esthree.Builder#region].
	/// This is provided only for convenience.
	enum Region {
		AF_SOUTH_1("af-south-1"),
		AP_EAST_1("ap-east-1"),
		AP_EAST_2("ap-east-2"),
		AP_NORTHEAST_1("ap-northeast-1"),
		AP_NORTHEAST_2("ap-northeast-2"),
		AP_NORTHEAST_3("ap-northeast-3"),
		AP_SOUTH_1("ap-south-1"),
		AP_SOUTH_2("ap-south-2"),
		AP_SOUTHEAST_1("ap-southeast-1"),
		AP_SOUTHEAST_2("ap-southeast-2"),
		AP_SOUTHEAST_3("ap-southeast-3"),
		AP_SOUTHEAST_4("ap-southeast-4"),
		AP_SOUTHEAST_5("ap-southeast-5"),
		AP_SOUTHEAST_6("ap-southeast-6"),
		AP_SOUTHEAST_7("ap-southeast-7"),

		CA_CENTRAL_1("ca-central-1"),
		CA_WEST_1("ca-west-1"),

		CN_NORTH_1("cn-north-1"),
		CN_NORTHWEST_1("cn-northwest-1"),

		EU_CENTRAL_1("eu-central-1"),
		EU_CENTRAL_2("eu-central-2"),
		EU_ISOE_WEST_1("eu-isoe-west-1"),
		EU_NORTH_1("eu-north-1"),
		EU_SOUTH_1("eu-south-1"),
		EU_SOUTH_2("eu-south-2"),
		EU_WEST_1("eu-west-1"),
		EU_WEST_2("eu-west-2"),
		EU_WEST_3("eu-west-3"),

		EUSC_DE_EAST_1("eusc-de-east-1"),

		IL_CENTRAL_1("il-central-1"),

		ME_CENTRAL_1("me-central-1"),
		ME_SOUTH_1("me-south-1"),

		MX_CENTRAL_1("mx-central-1"),

		SA_EAST_1("sa-east-1"),

		US_EAST_1("us-east-1"),
		US_EAST_2("us-east-2"),
		US_WEST_1("us-west-1"),
		US_WEST_2("us-west-2"),

		US_GOV_EAST_1("us-gov-east-1"),
		US_GOV_WEST_1("us-gov-west-1"),

		US_ISO_EAST_1("us-iso-east-1"),
		US_ISO_WEST_1("us-iso-west-1"),

		US_ISOB_EAST_1("us-isob-east-1"),
		US_ISOF_EAST_1("us-isof-east-1"),
		US_ISOF_SOUTH_1("us-isof-south-1"),

		AWS_CN_GLOBAL("aws-cn-global"),
		AWS_GLOBAL("aws-global"),
		AWS_ISO_B_GLOBAL("aws-iso-b-global"),
		AWS_ISO_E_GLOBAL("aws-iso-e-global"),
		AWS_ISO_F_GLOBAL("aws-iso-f-global"),
		AWS_ISO_GLOBAL("aws-iso-global"),
		AWS_US_GOV_GLOBAL("aws-us-gov-global");

		private final String identifier;

		Region(String identifier) {
			this.identifier = identifier;
		}

		/// Parse a [Region] from the provided string identifier.
		public static Optional<Region> from(String identifier) {
			return Arrays.stream(Region.values())
					.filter(region -> region.identifier.equals(identifier))
					.findFirst();
		}

		@Override
		public String toString() {
			return this.identifier;
		}
	}

	/// Represents an S3 bucket, as returned by e.g. `ListBuckets`.
	interface Bucket {

		/// The Amazon Resource Name (ARN) of the S3 bucket.
		/// @throws NoSuchElementException if field is missing from response
		String arn();

		/// The AWS region where the bucket is located.
		/// @throws NoSuchElementException if field is missing from response
		/// @throws IllegalStateException failed to parse response region
		Region region();

		/// Date the bucket was created (some bucket changes can update this).
		/// @throws NoSuchElementException if field is missing from response
		/// @throws DateTimeParseException failure to parse response timestamp
		Instant creation();

		/// The name of the bucket.
		/// @throws NoSuchElementException if field is missing from response
		String name();
	}
}