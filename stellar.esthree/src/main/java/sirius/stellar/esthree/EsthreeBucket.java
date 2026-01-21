package sirius.stellar.esthree;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

/// Represents an S3 bucket, as returned by e.g. `ListBuckets`.
public interface EsthreeBucket {

	/// The Amazon Resource Name (ARN) of the S3 bucket.
	/// @throws NoSuchElementException if field is missing from response
	String arn();

	/// The AWS region where the bucket is located.
	/// @throws NoSuchElementException if field is missing from response
	/// @throws IllegalStateException failed to parse response region
	Esthree.Region region();

	/// Date the bucket was created (some bucket changes can update this).
	/// @throws NoSuchElementException if field is missing from response
	/// @throws DateTimeParseException failure to parse response timestamp
	Instant creation();

	/// The name of the bucket.
	/// @throws NoSuchElementException if field is missing from response
	String name();
}