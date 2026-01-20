package sirius.stellar.esthree;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

/// Domain implementation of [EsthreeBucket] wrapping an [Element]
/// response from bucket-related methods, such as AWS `ListBuckets`.
final class DEsthreeBucket implements EsthreeBucket {

	private final Element bucket;

	DEsthreeBucket(Node bucket) {
		if (!(bucket instanceof Element)) throw new IllegalStateException();
		this.bucket = (Element) bucket;
	}

	@Override
	public String arn() {
		return this.element("BucketArn");
	}

	@Override
	public EsthreeRegion region() {
		Optional<EsthreeRegion> region = EsthreeRegion.from(this.element("BucketRegion"));
		return region.orElseThrow(() -> new IllegalStateException("Unknown region '" + region + "'"));
	}

	@Override
	public Instant creation() {
		return Instant.parse(this.element("CreationDate"));
	}

	@Override
	public String name() {
		return this.element("Name");
	}

	/// Return the [String] text content of the element tag name in [#bucket].
	/// @throws NoSuchElementException if field is missing from response
	private String element(String name) {
		Node node = this.bucket.getElementsByTagName(name).item(0);
		if (node == null) throw new NoSuchElementException(name + " field missing in response");
		return node.getTextContent();
	}
}