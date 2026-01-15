package sirius.stellar.esthree.awssdk;

import io.avaje.http.client.HttpClient;
import sirius.stellar.esthree.Esthree;
import sirius.stellar.esthree.EsthreePayload;
import sirius.stellar.esthree.EsthreeSigner;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static software.amazon.awssdk.core.sync.RequestBody.fromInputStream;

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

	//#region putPayload
	@Override
	public void putPayload(String bucket, String key, EsthreePayload payload) {
		try {
			this.delegate.putObject(builder -> {
				builder.bucket(bucket);
				builder.key(key);

				builder.contentLength(payload.size());
				builder.contentType(payload.type());

				if (!payload.hash().isEmpty()) {
					builder.checksumAlgorithm(ChecksumAlgorithm.SHA256);
					builder.checksumSHA256(payload.hash());
				}
			}, fromInputStream(payload.stream(), payload.size()));
		} catch (S3Exception exception) {
			throw new AwsEsthreeException(exception);
		}
	}

    @Override
    public CompletableFuture<Void> putPayloadFuture(String bucket, String key, EsthreePayload payload) {
        return runAsync(() -> putPayload(bucket, key, payload));
    }
	//#endregion

	//#region existsPayload
	@Override
	public boolean existsPayload(String bucket, String key) {
		try {
			this.delegate.headObject(builder -> {
				builder.bucket(bucket);
				builder.key(key);
			});
			return true;
		} catch (NoSuchBucketException exception) {
			return false;
		} catch (S3Exception exception) {
			throw new AwsEsthreeException(exception);
		}
	}

    @Override
    public CompletableFuture<Boolean> existsPayloadFuture(String bucket, String key) {
        return supplyAsync(() -> existsPayload(bucket, key));
    }
	//#endregion

	//#region deletePayload
	@Override
	public void deletePayload(String bucket, String key) {
		try {
			this.delegate.deleteObject(builder -> {
				builder.bucket(bucket);
				builder.key(key);
			});
		} catch (S3Exception exception) {
			throw new AwsEsthreeException(exception);
		}
	}

    @Override
    public CompletableFuture<Void> deletePayloadFuture(String bucket, String key) {
        return runAsync(() -> deletePayload(bucket, key));
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
	public void release() {
		assert true;
	}

	@Override
	public void close() {
		this.delegate.close();
	}
}