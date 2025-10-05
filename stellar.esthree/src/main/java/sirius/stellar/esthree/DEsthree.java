package sirius.stellar.esthree;

import io.avaje.http.client.HttpClient;
import org.jspecify.annotations.Nullable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static sirius.stellar.esthree.Esthree.Region.*;
import static javax.xml.XMLConstants.*;

/// Default/domain implementation of [Esthree].
final class DEsthree implements Esthree {

	private final EsthreeSigner signer;
	private final HttpClient client;
	private final DocumentBuilder parser;

	DEsthree(EsthreeSigner signer, HttpClient client, DocumentBuilder parser) {
		this.signer = signer;
		this.client = client;
		this.parser = parser;
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
	public HttpClient httpClient() {
		return this.client;
	}
}

/// Default/domain implementation of [Esthree.Builder].
final class DEsthreeBuilder implements Esthree.Builder {

	private final HttpClient.Builder httpClientBuilder;

	private String region;
	private String endpoint;
	private boolean endpointOverride;

	@Nullable
	private String accessKey;

	@Nullable
	private String secretKey;

	DEsthreeBuilder() {
		this.httpClientBuilder = HttpClient.builder();

		this.region = US_EAST_1.toString();
		this.endpoint = "https://s3." + this.region + ".amazonaws.com";
		this.endpointOverride = false;
	}

	@Override
	public Esthree.Builder endpoint(String endpoint) {
		this.endpoint = endpoint;
		this.endpointOverride = true;
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
			factory.setAttribute(ACCESS_EXTERNAL_STYLESHEET, "");
			return new DEsthree(signer, this.httpClientBuilder.build(), factory.newDocumentBuilder());
		} catch (ParserConfigurationException exception) {
			throw new IllegalStateException("Failed to configure javax.xml DocumentBuilderFactory in Esthree Builder", exception);
		}
	}
}