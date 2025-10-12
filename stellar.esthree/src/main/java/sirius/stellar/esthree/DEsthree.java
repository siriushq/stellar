package sirius.stellar.esthree;

import io.avaje.http.client.*;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.*;
import static javax.xml.XMLConstants.*;
import static javax.xml.transform.OutputKeys.*;
import static sirius.stellar.esthree.Esthree.Region.*;

/// Domain implementation of [Esthree].
final class DEsthree implements Esthree {

	/// The `xmlns` property, required on the root tag to make S3 requests with bodies.
	private static final String xmlns = "http://s3.amazonaws.com/doc/2006-03-01/";

	private final EsthreeSigner signer;
	private final HttpClient client;
	private final DocumentBuilder parser;
	private final Transformer transformer;

	private final String region;

	private final String endpoint;
	private final boolean endpointVirtual;

	DEsthree(EsthreeSigner signer, HttpClient client, DocumentBuilder parser, Transformer transformer, String region, String endpoint, boolean endpointVirtual) {
		this.signer = signer;
		this.client = client;
		this.parser = parser;
		this.transformer = transformer;

		this.region = region;

		this.endpoint = endpoint;
		this.endpointVirtual = endpointVirtual;
	}

	@Override
	public void close() {
		this.client.close();
	}

	@Override
	public Stream<Bucket> buckets() {
		return Stream.empty();
	}

	@Override
	public CompletableFuture<Stream<Bucket>> bucketsFuture() {
		return null;
	}

	@Override
	public void createBucket(String name) {
		this.createBucketInternal(name)
				.asVoid();
	}

	@Override
	public CompletableFuture<Void> createBucketFuture(String name) {
		return this.createBucketInternal(name)
				.async()
				.asVoid()
				.thenApply(HttpResponse::body);
	}

	private HttpClientResponse createBucketInternal(String name) {
		HttpClientRequest request = this.client.request();
		request.url(UrlBuilder.of(this.endpoint)
				.path(name)
				.build());

		if (this.endpointVirtual) {
			String endpoint = this.endpoint.replaceFirst("://", "://" + name + ".");
			request.url(endpoint);
		}
		if (!this.region.equals(US_EAST_1.toString())) {
			Document document = this.parser.newDocument();
			Element root = document.createElementNS(xmlns, "CreateBucketConfiguration");

			Element location = document.createElement("LocationConstraint");
			location.setTextContent(name);

			root.appendChild(location);
			document.appendChild(root);

			request.body(this.write(document));
		}

		this.signer.sign(request, request.bodyContent().orElse(BodyContent.of(new byte[0])));
		return request.PUT();
	}

	/// Write the provided document to a [String].
	///
	/// This buffers the entire contents of the document in-memory, as [HttpRequest.BodyPublisher]
	/// provides no facility for streaming from `javax.xml`, without instantiating another thread
	/// (which would be a heavier operation).
	private String write(Document document) {
		try (Writer writer = new StringWriter()) {
			this.transformer.transform(new DOMSource(document), new StreamResult(writer));
			return writer.toString();
		} catch (TransformerException | IOException exception) {
			throw new IllegalStateException("Failed to transform document body for request in Esthree", exception);
		}
	}

	@Override
	public HttpClient httpClient() {
		return this.client;
	}
}

/// Domain implementation of [Esthree.Builder].
final class DEsthreeBuilder implements Esthree.Builder {

	private final HttpClient.Builder httpClientBuilder;

	private String region;
	private String endpoint;
	private boolean endpointOverride;
	private boolean endpointVirtual;

	private @Nullable String accessKey;
	private @Nullable String secretKey;

	DEsthreeBuilder() {
		this.httpClientBuilder = HttpClient.builder();

		this.region = US_EAST_1.toString();
		this.endpoint = "https://s3." + this.region + ".amazonaws.com";
		this.endpointOverride = false;
		this.endpointVirtual = true;
	}

	@Override
	public Esthree.Builder endpoint(String endpoint, boolean virtual) {
		this.endpoint = endpoint;
		this.endpointOverride = true;
		this.endpointVirtual = virtual;
		return this;
	}

	@Override
	public Esthree.Builder region(String region) {
		if (this.endpointOverride) return this;
		this.endpoint = "https://s3." + region + ".amazonaws.com";
		this.region = region;
		return this;
	}

	@Override
	public Esthree.Builder credentials(String accessKey, String secretKey) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		return this;
	}

	@Override
	public HttpClient.Builder httpClientBuilder() {
		return this.httpClientBuilder;
	}

	@Override
	public Esthree build() {
		if (this.accessKey == null
			|| this.accessKey.isEmpty()
			|| this.secretKey == null
			|| this.secretKey.isEmpty()) throw new IllegalStateException("No credentials provided to Esthree Builder");

		EsthreeSigner signer = EsthreeSigner.create(this.accessKey, this.secretKey, this.region);
		this.httpClientBuilder.baseUrl(this.endpoint);

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
			factory.setXIncludeAware(false);
			factory.setExpandEntityReferences(false);

			factory.setFeature(FEATURE_SECURE_PROCESSING, true);
			factory.setAttribute(ACCESS_EXTERNAL_DTD, "");
			factory.setAttribute(ACCESS_EXTERNAL_SCHEMA, "");

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(ENCODING, UTF_8.name());

			return new DEsthree(
					signer,
					this.httpClientBuilder.build(),
					factory.newDocumentBuilder(),
					transformer,
					this.region,
					this.endpoint,
					this.endpointVirtual);
		} catch (ParserConfigurationException exception) {
			throw new IllegalStateException("Failed to configure javax.xml DocumentBuilderFactory in Esthree Builder", exception);
		} catch (TransformerConfigurationException exception) {
			throw new IllegalStateException("Failed to configure javax.xml Transformer in Esthree Builder", exception);
		}
	}
}