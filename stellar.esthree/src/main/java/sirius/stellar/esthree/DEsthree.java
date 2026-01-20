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
import java.io.*;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.net.http.HttpRequest.BodyPublishers.fromPublisher;
import static sirius.stellar.esthree.EsthreeRegion.US_EAST_1;

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
	public Stream<EsthreeBucket> buckets() {
		return this.bucketsPaginator(this.client.request()).stream();
	}

	@Override
	public Stream<CompletableFuture<EsthreeBucket>> bucketsFuture() {
		return this.bucketsPaginator(this.client.request()).streamFuture();
	}

	@Override
	public Stream<EsthreeBucket> buckets(String prefix) {
		HttpClientRequest request = this.client.request();
		request.queryParam("prefix", prefix);
		return this.bucketsPaginator(request).stream();
	}

	@Override
	public Stream<CompletableFuture<EsthreeBucket>> bucketsFuture(String prefix) {
		HttpClientRequest request = this.client.request();
		request.queryParam("prefix", prefix);
		return this.bucketsPaginator(request).streamFuture();
	}

	/// Return a paginator used to execute the AWS `ListBuckets` method.
	/// Used by [#buckets] and [#bucketsFuture].
	private DEsthreePaginator<EsthreeBucket> bucketsPaginator(HttpClientRequest request) {
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

		this.signer.sign("PUT", request, request.bodyContent().orElse(BodyContent.of(new byte[0])));
		return request.PUT();
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

		this.signer.sign("DELETE", request, BodyContent.of(new byte[0]));
		return request.DELETE();
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

		this.signer.sign("HEAD", request, BodyContent.of(new byte[0]));
		return request.HEAD();
	}
	//#endregion

	//#region putPayload
	@Override
	public void putPayload(String bucket, String key, EsthreePayload payload) {
		byte[] body = this.putPayloadResponse(bucket, key, payload)
				.asByteArray()
				.body();
		this.errorResponse(body);
	}

	@Override
    public CompletableFuture<Void> putPayloadFuture(String bucket, String key, EsthreePayload payload) {
		return this.putPayloadResponse(bucket, key, payload)
				.async()
				.asByteArray()
				.thenApply(HttpResponse::body)
				.thenAccept(this::errorResponse);
	}

	/// Execute the AWS `PutObject` method and return the associated [HttpClientResponse].
	/// Used by [#putPayload] and [#putPayloadFuture].
	private HttpClientResponse putPayloadResponse(String bucket, String key, EsthreePayload payload) {
		HttpClientRequest request = this.client.request();
		this.endpoint(request, bucket);
		request.path(key);

		if (!payload.hash().isEmpty()) {
			this.signer.sign("PUT", request, payload.hash());
			request.body(payload.stream());
			return request.PUT();
		}

		this.signer.sign("PUT", request, payload.stream(), payload.size());
		return request.PUT();
	}
	//#endregion

	//#region getPayload
	@Override
	public EsthreePayload getPayload(String bucket, String key) {
		try {
			HttpResponse<InputStream> response = this.getPayloadResponse(bucket, key)
					.asInputStream();
			return getPayloadParse(response);
		} catch (HttpException exception) {
			this.errorResponse(exception.bodyAsBytes());
			throw EsthreeException.of(exception);
		}
	}

	@Override
    public CompletableFuture<EsthreePayload> getPayloadFuture(String bucket, String key) {
		return this.getPayloadResponse(bucket, key)
				.async()
				.asInputStream()
				.thenApply(this::getPayloadParse);
	}

	/// Execute the AWS `GetObject` method and return the associated [HttpClientResponse].
	/// Used by [#getPayload] and [#getPayload].
	private HttpClientResponse getPayloadResponse(String bucket, String key) {
		HttpClientRequest request = this.client.request();
		this.endpoint(request, bucket);
		request.path(key);

		this.signer.sign("GET", request, BodyContent.of(new byte[0]));
		return request.GET();
	}

	/// Parse a [HttpClientResponse] (as returned by [#getPayloadResponse])
	/// to a viewer instance of [EsthreePayload] for the response values.
	private EsthreePayload getPayloadParse(HttpResponse<InputStream> response) {
		HttpHeaders headers = response.headers();
		InputStream stream = response.body();

		String type = headers.firstValue("Content-Type").orElse("");
		long size = headers.firstValueAsLong("Content-Length").orElse(-1);

		Optional<String> optional = headers.firstValue("x-amz-content-sha256");
		if (optional.isPresent()) {
			String hash = optional.get();
			return EsthreePayload.create(type, size, hash, stream);
		}

		return EsthreePayload.create(type, size, stream);
	}
	//#endregion

	//#region existsPayload
	@Override
	public boolean existsPayload(String bucket, String key) {
		try {
			HttpResponse<byte[]> response = this.existsPayloadResponse(bucket, key)
					.asByteArray();
			this.errorResponse(response.body());
			return response.statusCode() == 200;
		} catch (HttpException exception) {
			this.errorResponse(exception.bodyAsBytes());
			return false;
		}
	}

	@Override
	public CompletableFuture<Boolean> existsPayloadFuture(String bucket, String key) {
		return this.existsPayloadResponse(bucket, key)
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

	/// Execute the AWS `HeadObject` method and return the associated [HttpClientResponse].
	/// Used by [#existsPayload] and [#existsPayloadFuture].
	private HttpClientResponse existsPayloadResponse(String bucket, String key) {
		HttpClientRequest request = this.client.request();
		this.endpoint(request, bucket);
		request.path(key);

		this.signer.sign("HEAD", request, BodyContent.of(new byte[0]));
		return request.HEAD();
	}
	//#endregion

	//#region deletePayload
	@Override
	public void deletePayload(String bucket, String key) {
		byte[] body = this.deletePayloadResponse(bucket, key)
				.asByteArray()
				.body();
		this.errorResponse(body);
	}

	@Override
    public CompletableFuture<Void> deletePayloadFuture(String bucket, String key) {
		return this.deletePayloadResponse(bucket, key)
				.async()
				.asByteArray()
				.thenApply(HttpResponse::body)
				.thenAccept(this::errorResponse);
	}

	/// Execute the AWS `PutObject` method and return the associated [HttpClientResponse].
	/// Used by [#putPayload] and [#putPayloadFuture].
	private HttpClientResponse deletePayloadResponse(String bucket, String key) {
		HttpClientRequest request = this.client.request();
		this.endpoint(request, bucket);
		request.path(key);

		this.signer.sign("DELETE", request, BodyContent.of(new byte[0]));
		return request.DELETE();
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

	/// Set the endpoint of the provided [HttpClientRequest] for operations
	/// relating to a bucket.
	///
	/// This will either use virtual-host based or path based addressing,
	/// depending on [#endpointVirtual].
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
	public void release() {
		this.parser.remove();
		this.transformer.remove();
		this.signer.release();
	}

	@Override
	public void close() {
		this.client.close();
	}
}