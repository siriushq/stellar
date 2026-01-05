package sirius.stellar.esthree;

import io.avaje.http.client.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static sirius.stellar.esthree.Esthree.Region.US_EAST_1;

/// Domain implementation of [Esthree].
final class DEsthree implements Esthree {

	/// The `xmlns` property, required on the root tag to make S3 requests with bodies.
	private static final String XMLNS = "http://s3.amazonaws.com/doc/2006-03-01/";

	private final EsthreeSigner signer;
	private final HttpClient client;

	private final ThreadLocal<DocumentBuilder> parser;
	private final ThreadLocal<Transformer> transformer;

	private final String region;

	private final String endpoint;
	private final boolean endpointVirtual;

	DEsthree(EsthreeSigner signer, HttpClient client, ThreadLocal<DocumentBuilder> parser, ThreadLocal<Transformer> transformer, String region, String endpoint, boolean endpointVirtual) {
		this.signer = signer;
		this.client = client;
		this.parser = parser;
		this.transformer = transformer;

		this.region = region;

		this.endpoint = endpoint;
		this.endpointVirtual = endpointVirtual;
	}

	//#region buckets*
	@Override
	public Stream<Bucket> buckets() {
		return this.bucketsPaginator(this.client.request()).stream();
	}

	@Override
	public Stream<CompletableFuture<Bucket>> bucketsFuture() {
		return this.bucketsPaginator(this.client.request()).streamFuture();
	}

	@Override
	public Stream<Bucket> buckets(String prefix) {
		HttpClientRequest request = this.client.request();
		request.queryParam("prefix", prefix);
		return this.bucketsPaginator(request).stream();
	}

	@Override
	public Stream<CompletableFuture<Bucket>> bucketsFuture(String prefix) {
		HttpClientRequest request = this.client.request();
		request.queryParam("prefix", prefix);
		return this.bucketsPaginator(request).streamFuture();
	}

	/// Return a paginator used to execute the AWS `ListBuckets` method.
	/// Used by [#buckets] and [#bucketsFuture].
	private DEsthreePaginator<Bucket> bucketsPaginator(HttpClientRequest request) {
		String continuation = "ContinuationToken";
		return new DEsthreePaginator<>(this.parser, this.signer, continuation, request, document -> {
			NodeList bucketsTags = document.getElementsByTagName("Buckets");
			if (bucketsTags.getLength() == 0) return 0;

			Node buckets = bucketsTags.item(0);
			return buckets.getChildNodes().getLength();
		}, (document, index) -> {
			NodeList bucketsTags = document.getElementsByTagName("Buckets");
			if (bucketsTags.getLength() == 0) throw new IllegalStateException();//TODO

			Node buckets = bucketsTags.item(0);
			Node bucket = buckets.getChildNodes().item(index);

			return new DEsthreeBucket(bucket);
		});
	}
	//#endregion

	//#region createBucket*
	@Override
	public void createBucket(String name) {
		byte[] body = this.createBucketResponse(name)
				.asByteArray()
				.body();
		this.errorResponse(body);
	}

	@Override
	public CompletableFuture<Void> createBucketFuture(String name) {
		return this.createBucketResponse(name)
				.async()
				.asByteArray()
				.thenApply(HttpResponse::body)
				.thenAccept(this::errorResponse);
	}

	/// Execute the AWS `CreateBucket` method and return the associated [HttpClientResponse].
	/// Used by [#createBucket] and [#createBucketFuture].
	private HttpClientResponse createBucketResponse(String name) {
		HttpClientRequest request = this.client.request();
		this.endpoint(request, name);

		if (!this.region.equals(US_EAST_1.toString())) {
			Document document = this.parser.get().newDocument();
			Element root = document.createElementNS(XMLNS, "CreateBucketConfiguration");

			Element location = document.createElement("LocationConstraint");
			location.setTextContent(name);

			root.appendChild(location);
			document.appendChild(root);

			request.body(BodyContent.of("application/xml", this.write(document)));
		}

		try (EsthreeSigner signer = this.signer.acquire()) {
			signer.sign("PUT", request, request.bodyContent().orElse(BodyContent.of(new byte[0])));
			return request.PUT();
		}
	}
	//#endregion

	//#region deleteBucket*
	@Override
	public void deleteBucket(String name) {
		byte[] body = this.deleteBucketResponse(name)
				.asByteArray()
				.body();
		this.errorResponse(body);
	}

	@Override
	public CompletableFuture<Void> deleteBucketFuture(String name) {
		return this.deleteBucketResponse(name)
				.async()
				.asByteArray()
				.thenApply(HttpResponse::body)
				.thenAccept(this::errorResponse);
	}

	/// Execute the AWS `DeleteBucket` method and return the associated [HttpClientResponse].
	/// Used by [#deleteBucket] and [#deleteBucketFuture].
	private HttpClientResponse deleteBucketResponse(String name) {
		HttpClientRequest request = this.client.request();
		this.endpoint(request, name);

		try (EsthreeSigner signer = this.signer.acquire()) {
			signer.sign("DELETE", request, BodyContent.of(new byte[0]));
			return request.DELETE();
		}
	}
	//#endregion

	//#region existsBucket
	@Override
	public boolean existsBucket(String name) {
		try {
			HttpResponse<byte[]> response = this.existsBucketResponse(name).asByteArray();
			this.errorResponse(response.body());
			return response.statusCode() == 200;
		} catch (HttpException exception) {
			this.errorResponse(exception.bodyAsBytes());
			return false;
		}
	}

	@Override
	public CompletableFuture<Boolean> existsBucketFuture(String name) {
		return this.existsBucketResponse(name)
				.async()
				.asByteArray()
				.thenApply(response -> {
					this.errorResponse(response.body());
					return (response.statusCode() == 200);
				})
				.exceptionally(throwable -> {
					if (!(throwable instanceof HttpException)) return false;
					HttpException exception = (HttpException) throwable;
					this.errorResponse(exception.bodyAsBytes());
					return false;
				});
	}

	/// Execute the AWS `HeadBucket` method and return the associated [HttpClientResponse].
	/// Used by [#existsBucket] and [#existsBucketFuture].
	private HttpClientResponse existsBucketResponse(String name) {
		HttpClientRequest request = this.client.request();
		this.endpoint(request, name);

		try (EsthreeSigner signer = this.signer.acquire()) {
			signer.sign("HEAD", request, BodyContent.of(new byte[0]));
			return request.HEAD();
		}
	}
	//#endregion

	/// Write the provided document to a [String].
	///
	/// This buffers the entire contents of the document in-memory, as [HttpRequest.BodyPublisher]
	/// provides no facility for streaming from `javax.xml`, without instantiating another thread
	/// (which would be a heavier operation).
	private String write(Document document) {
		try (Writer writer = new StringWriter()) {
			this.transformer.get().transform(new DOMSource(document), new StreamResult(writer));
			return writer.toString();
		} catch (TransformerException | IOException exception) {
			throw new IllegalStateException("Failed to transform document body for request in Esthree", exception);
		}
	}

	/// Set the endpoint of the provided [HttpClientRequest] for bucket operations.
	/// This will either use virtual-host based or path based addressing, depending on [#endpointVirtual].
	private void endpoint(HttpClientRequest request, String bucket) {
		request.url(UrlBuilder.of(this.endpoint)
				.path(bucket)
				.build());

		if (this.endpointVirtual) {
			String endpoint = this.endpoint.replaceFirst("://", "://" + bucket + ".");
			request.url(endpoint);
		}
	}

	/// Assert that the provided body does not contain an error response.
	/// Throws [EsthreeException] if an error is found, using the contents of the response.
	private void errorResponse(byte[] body) {
		try {
			if (body.length == 0) return;
			Document document = this.parser.get().parse(new ByteArrayInputStream(body));

			if (!EsthreeException.detected(document)) return;
			throw EsthreeException.of(document);
		} catch (IOException | SAXException exception) {
			throw EsthreeException.of(this.parser.get().newDocument(), exception);
		}
	}

	@Override
	public HttpClient httpClient() {
		return this.client;
	}

	@Override
	public void close() {
		this.client.close();
	}
}