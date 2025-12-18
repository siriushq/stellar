package sirius.stellar.esthree.awssdk;

import sirius.stellar.esthree.EsthreeException;
import software.amazon.awssdk.services.s3.model.S3Exception;

/// Implementation of [EsthreeException] that wraps the AWS SDK v2
/// [S3Exception] and propagates information in a compatible manner.
///
/// @see AwsEsthreeBuilder
final class AwsEsthreeException extends EsthreeException {

	private static final long serialVersionUID = 6095808162312969695L;

	AwsEsthreeException(S3Exception exception) {
        super(
			exception,
			exception.awsErrorDetails() != null
					? exception.awsErrorDetails().errorCode()
					: "Unknown",
            exception.awsErrorDetails() != null
					? exception.awsErrorDetails().errorMessage()
					: "N/A",
            "Unknown",
            exception.requestId(),
            exception.extendedRequestId()
        );
    }
}