package sirius.stellar.esthree;

import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow;

/// Implementation of [HttpRequest.BodyPublisher] purpose-specific for writing
/// signed streams with a known length. This does not sign the stream.
final class DEsthreeSignedPublisher implements BodyPublisher {

	private final BodyPublisher delegate;
	private final long size;

	DEsthreeSignedPublisher(InputStream stream, long size) {
		this.delegate = BodyPublishers.ofInputStream(() -> stream);
		this.size = size;
	}

	@Override
	public void subscribe(Flow.Subscriber<? super ByteBuffer> subscriber) {
		this.delegate.subscribe(subscriber);
	}

	@Override
	public long contentLength() {
		return this.size;
	}
}