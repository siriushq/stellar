package sirius.stellar.esthree.awssdk;

import io.avaje.http.client.HttpClient;
import sirius.stellar.esthree.Esthree;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URI;
import java.util.ServiceLoader;
import java.util.function.Function;

/// Implementation of [Esthree.Builder] that builds and provides instances of
/// [Esthree] backed by the AWS SDK v2, and automatically takes precedence when
/// available on the class-path or module-path.
///
/// The [#httpClientBuilder] method provided by this implementation returns a
/// completely ignored/discarded builder instance, if [io.avaje.http.client]
/// is available on the module-path (otherwise `throw`ing).
///
/// Casting to this type, when using the builder interface, can allow one to
/// modify the underlying AWS SDK v2 builder with [#configureDelegate], or the
/// metrics interceptor (if it, or any behavior of it, is undesirable).
///
/// @see AwsEsthree
public final class AwsEsthreeBuilder implements Esthree.Builder {

	private AwsEsthreeInterceptor interceptor;
	private S3ClientBuilder delegate;

	/// Constructor used by [ServiceLoader] for instantiation.
	/// This should never be manually / externally invoked.
	public AwsEsthreeBuilder() {
		this.interceptor = new AwsEsthreeInterceptor(false);
		this.delegate = S3Client.builder().overrideConfiguration(this.interceptor);
	}

	/// Configure the underlying [S3ClientBuilder], if required.
	/// Most people should never use this method.
	///
	/// A generic type argument can be provided to allow chaining of this
	/// method, where different invocations may return other subclasses
	/// of [S3ClientBuilder]:
	///
	/// ```
	/// ((AwsEsthreeBuilder) Esthree.builder())
	/// 	.configureDelegate(builder -> builder
	/// 		.credentialsProvider(...))
	/// 		.endpointProvider(...))
	/// 	.configureDelegate(MyS3ClientBuilder::new);
 	/// ```
	public <T extends S3ClientBuilder>
	void configureDelegate(Function<S3ClientBuilder, T> configurer) {
		this.delegate = configurer.apply(this.delegate);
	}

	/// Disable the [AwsEsthreeInterceptor] if required, to prevent
	/// metrics from being collected.
	///
	/// Most people should never use this method.
	public void disableInterceptor() {
		this.interceptor = new AwsEsthreeInterceptor(true);
	}

	@Override
	public Esthree.Builder endpoint(String endpoint, boolean virtual) {
		this.delegate = this.delegate.endpointOverride(URI.create(endpoint));
		this.delegate = this.delegate.forcePathStyle(!virtual);
		return this;
	}

	@Override
	public Esthree.Builder region(String region) {
		this.delegate = this.delegate.region(Region.of(region));
		return this;
	}

	@Override
	public Esthree.Builder credentials(String accessKey, String secretKey) {
		AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
		AwsCredentialsProvider provider = StaticCredentialsProvider.create(credentials);

		this.delegate = this.delegate.credentialsProvider(provider);
		return this;
	}

	@Override
	public HttpClient.Builder httpClientBuilder() {
		try {
			return HttpClient.builder();
		} catch (NoClassDefFoundError error) {
			throw new UnsupportedOperationException(error);
		}
	}

	@Override
	public Esthree build() {
		return new AwsEsthree(this.delegate.build(), this.interceptor);
	}
}