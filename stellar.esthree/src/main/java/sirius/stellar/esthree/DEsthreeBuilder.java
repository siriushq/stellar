package sirius.stellar.esthree;

import io.avaje.http.client.HttpClient;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import java.util.StringJoiner;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static java.lang.ThreadLocal.withInitial;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.xml.XMLConstants.*;
import static javax.xml.transform.OutputKeys.ENCODING;
import static sirius.stellar.esthree.EsthreeRegion.US_EAST_1;

/// Domain implementation of [Esthree.Builder].
final class DEsthreeBuilder implements Esthree.Builder {

	private final HttpClient.Builder httpClientBuilder;

	private String region;

	private String endpoint;
	private boolean endpointOverride;
	private boolean endpointVirtual;

	private String accessKey;
	private String secretKey;

	DEsthreeBuilder() {
		this.httpClientBuilder = HttpClient.builder();

		this.region = getProperty("aws.region");
		if (this.region == null) this.region = getenv().get("AWS_REGION");
		if (this.region == null) this.region = US_EAST_1.toString();

		this.accessKey = getProperty("aws.accessKeyId");
		if (this.accessKey == null) this.accessKey = getenv().get("AWS_ACCESS_KEY_ID");
		if (this.accessKey == null) this.accessKey = "";

		this.secretKey = getProperty("aws.secretAccessKey");
		if (this.secretKey == null) this.secretKey = getenv().get("AWS_SECRET_ACCESS_KEY");
		if (this.secretKey == null) this.secretKey = "";

		this.endpoint = getProperty("aws.endpointUrl");
		if (this.endpoint == null) this.endpoint = getenv().get("AWS_ENDPOINT_URL");

		this.endpointOverride = true;
		this.endpointVirtual = false;

		if (this.endpoint == null) {
			this.endpoint = ("https://s3." + this.region + ".amazonaws.com");
			this.endpointOverride = false;
			this.endpointVirtual = true;
		}
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

	/// Creates a hardened [DocumentBuilderFactory].
	private static DocumentBuilderFactory documentBuilderFactory() {
		try {
			DocumentBuilderFactory result = DocumentBuilderFactory.newDefaultInstance();

			result.setXIncludeAware(false);
			result.setExpandEntityReferences(false);

			result.setFeature(FEATURE_SECURE_PROCESSING, true);
			result.setAttribute(ACCESS_EXTERNAL_DTD, "");
			result.setAttribute(ACCESS_EXTERNAL_SCHEMA, "");

			return result;
		} catch (ParserConfigurationException exception) {
			throw new IllegalStateException("Failed to configure javax.xml DocumentBuilderFactory", exception);
		}
	}

	/// Creates a [DocumentBuilder] using the provided factory, for thread-local instantiation.
	private static DocumentBuilder documentBuilder(DocumentBuilderFactory factory) {
		try {
			return factory.newDocumentBuilder();
		} catch (ParserConfigurationException exception) {
			throw new IllegalStateException("Failed to create thread-local javax.xml DocumentBuilder", exception);
		}
	}

	/// Creates a hardened [TransformerFactory].
	private static TransformerFactory transformerFactory() {
		try {
			TransformerFactory result = TransformerFactory.newInstance();

			result.setFeature(FEATURE_SECURE_PROCESSING, true);
			result.setAttribute(ACCESS_EXTERNAL_DTD, "");
			result.setAttribute(ACCESS_EXTERNAL_STYLESHEET, "");

			return result;
		} catch (TransformerConfigurationException exception) {
			throw new IllegalStateException("Failed to configure javax.xml TransformerFactory", exception);
		}
	}

	/// Creates a [Transformer] using the provided factory, for thread-local instantiation.
	private static Transformer transformer(TransformerFactory factory) {
		try {
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(ENCODING, UTF_8.name());
			return transformer;
		} catch (TransformerConfigurationException exception) {
			throw new IllegalStateException("Failed to create thread-local javax.xml Transformer", exception);
		}
	}

	@Override
	public Esthree build() {
		if (this.accessKey.isEmpty() || this.secretKey.isEmpty()) {
			throw new IllegalStateException(new StringJoiner(", ")
					.add("No credentials provided")
					.add("accessKey.length = " + this.accessKey.length())
					.add("secretKey.length = " + this.secretKey.length())
					.toString());
		}

		EsthreeSigner signer = EsthreeSigner.create(this.accessKey, this.secretKey, this.region);
		HttpClient client = this.httpClientBuilder
				.baseUrl(this.endpoint)
				.build();

		DocumentBuilderFactory documentBuilderFactory = documentBuilderFactory();
		TransformerFactory transformerFactory = transformerFactory();

		ThreadLocal<DocumentBuilder> parser = withInitial(() -> documentBuilder(documentBuilderFactory));
		ThreadLocal<Transformer> transformer = withInitial(() -> transformer(transformerFactory));

		return new DEsthree(signer, client, parser, transformer, this.region, this.endpoint, this.endpointVirtual);
	}
}