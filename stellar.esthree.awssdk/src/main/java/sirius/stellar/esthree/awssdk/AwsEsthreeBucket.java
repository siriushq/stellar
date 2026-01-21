package sirius.stellar.esthree.awssdk;

import sirius.stellar.esthree.Esthree;
import sirius.stellar.esthree.EsthreeBucket;

import java.time.Instant;
import java.util.Optional;

/// Implementation of [EsthreeBucket] delegating to AWS SDK v2,
/// wrapping a [software.amazon.awssdk.services.s3.model.Bucket].
///
/// @see AwsEsthreeBuilder
final class AwsEsthreeBucket implements EsthreeBucket {

	private final software.amazon.awssdk.services.s3.model.Bucket delegate;

	AwsEsthreeBucket(software.amazon.awssdk.services.s3.model.Bucket delegate) {
		this.delegate = delegate;
	}

	@Override
	public String arn() {
		return this.delegate.bucketArn();
	}

	@Override
	public Esthree.Region region() {
		Optional<Esthree.Region> region = Esthree.Region.from(this.delegate.bucketRegion());
		return region.orElseThrow(() -> new IllegalStateException("Unknown region '" + region + "'"));
	}

	@Override
	public Instant creation() {
		return this.delegate.creationDate();
	}

	@Override
	public String name() {
		return this.delegate.name();
	}
}