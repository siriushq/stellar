package sirius.stellar.esthree;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.time.Instant;

/// Domain implementation of [Esthree.Bucket] wrapping an [Element]
/// response from bucket-related methods, such as AWS `ListBuckets`.
final class DEsthreeBucket implements Esthree.Bucket {

	private final Element bucket;

	DEsthreeBucket(Node bucket) {
		if (!(bucket instanceof Element)) throw new IllegalStateException();
		this.bucket = (Element) bucket;
	}

	@Override
	public String arn() {
		return this.bucket.getElementsByTagName("BucketArn")
				.item(0)
				.getTextContent();
	}

	@Override
	public String region() {
		return this.bucket.getElementsByTagName("BucketRegion")
				.item(0)
				.getTextContent();
	}

	@Override
	public Instant creation() {
		return Instant.parse(this.bucket.getElementsByTagName("CreationDate")
				.item(0)
				.getTextContent());
	}

	@Override
	public String name() {
		return this.bucket.getElementsByTagName("Name")
				.item(0)
				.getTextContent();
	}
}