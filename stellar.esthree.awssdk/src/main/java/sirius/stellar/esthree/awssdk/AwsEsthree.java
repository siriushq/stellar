package sirius.stellar.esthree.awssdk;

import io.avaje.http.client.HttpClient;
import sirius.stellar.esthree.Esthree;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.*;
import static java.util.concurrent.CompletableFuture.runAsync;

/// Implementation of [Esthree] that delegates to the AWS SDK v2.
/// @see AwsEsthreeBuilder
final class AwsEsthree implements Esthree {

	private final S3Client delegate;
	private final AwsEsthreeInterceptor interceptor;

	AwsEsthree(S3Client delegate, AwsEsthreeInterceptor interceptor) {
		this.delegate = delegate;
		this.interceptor = interceptor;
	}

	//#region buckets*
	@Override
	public Stream<Bucket> buckets() {
		try {
			return this.delegate.listBucketsPaginator()
					.stream()
					.map(ListBucketsResponse::buckets)
					.flatMap(List::stream)
					.map(AwsEsthreeBucket::new);
		} catch (S3Exception exception) {
			throw new AwsEsthreeException(exception);
		}
	}

	@Override
	public Stream<CompletableFuture<Bucket>> bucketsFuture() {
		return buckets().map(CompletableFuture::completedFuture);
	}

	@Override
	public Stream<Bucket> buckets(String prefix) {
		try {
			return this.delegate.listBuckets(builder -> builder.prefix(prefix))
					.buckets()
					.stream()
					.map(AwsEsthreeBucket::new);
		} catch (S3Exception exception) {
			throw new AwsEsthreeException(exception);
		}
	}

	@Override
	public Stream<CompletableFuture<Bucket>> bucketsFuture(String prefix) {
		return buckets(prefix).map(CompletableFuture::completedFuture);
	}
	//#endregion

	//#region createBucket*
	@Override
	public void createBucket(String name) {
		try {
			this.delegate.createBucket(builder -> builder.bucket(name));
		} catch (S3Exception exception) {
			throw new AwsEsthreeException(exception);
		}
	}

	@Override
	public CompletableFuture<Void> createBucketFuture(String name) {
		return runAsync(() -> createBucket(name));
	}
	//#endregion

	//#region deleteBucket*
	@Override
	public void deleteBucket(String name) {
		try {
			this.delegate.deleteBucket(builder -> builder.bucket(name));
		} catch (S3Exception exception) {
			throw new AwsEsthreeException(exception);
		}
	}

	@Override
	public CompletableFuture<Void> deleteBucketFuture(String name) {
		return runAsync(() -> deleteBucket(name));
	}
	//#endregion

	//#region existsBucket
	@Override
	public boolean existsBucket(String name) {
		try {
			this.delegate.headBucket(builder -> builder.bucket(name));
			return true;
		} catch (NoSuchBucketException exception) {
			return false;
		} catch (S3Exception exception) {
			throw new AwsEsthreeException(exception);
		}
	}

    @Override
    public CompletableFuture<Boolean> existsBucketFuture(String name) {
        return supplyAsync(() -> existsBucket(name));
    }
	//#endregion

	@Override
	public HttpClient httpClient() {
		try {
			return HttpClient.builder().build();
		} catch (NoClassDefFoundError error) {
			throw new UnsupportedOperationException(error);
		}
	}

	@Override
	public HttpClient.Metrics metrics() {
		return this.interceptor;
	}

	@Override
	public HttpClient.Metrics metrics(boolean reset) {
		return this.interceptor.reset();
	}

	@Override
	public void close() {
		this.delegate.close();
	}
}