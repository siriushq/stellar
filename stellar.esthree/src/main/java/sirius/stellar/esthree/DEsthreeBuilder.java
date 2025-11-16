package sirius.stellar.esthree;

import io.avaje.http.client.HttpClient;
import org.jspecify.annotations.Nullable;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import static java.lang.System.*;
import static java.nio.charset.StandardCharsets.*;
import static javax.xml.XMLConstants.*;
import static javax.xml.transform.OutputKeys.*;
import static sirius.stellar.esthree.Esthree.Region.*;

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

		String region = getProperty("aws.region", getenv().get("AWS_REGION"));
		String accessKey = getProperty("aws.accessKeyId", getenv().get("AWS_ACCESS_KEY_ID"));
		String secretKey = getProperty("aws.secretAccessKey", getenv().get("AWS_SECRET_ACCESS_KEY"));
		String endpoint = getProperty("aws.endpointUrl", getenv().get("AWS_ENDPOINT_URL"));

		this.region = (region != null) ? region : US_EAST_1.toString();
		this.endpoint = (endpoint != null) ? endpoint : ("https://s3." + this.region + ".amazonaws.com");
		this.endpointOverride = (endpoint != null);
		this.endpointVirtual = (endpoint == null);

		this.accessKey = accessKey;
		this.secretKey = secretKey;
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
		if (this.endpointOverride)
			return this;
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
		if (this.accessKey == null || this.accessKey.isEmpty() || this.secretKey == null || this.secretKey.isEmpty())
			throw new IllegalStateException("No credentials provided to Esthree Builder");

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

			return new DEsthree(signer, this.httpClientBuilder.build(), factory.newDocumentBuilder(), transformer, this.region, this.endpoint, this.endpointVirtual);
		} catch (ParserConfigurationException exception) {
			throw new IllegalStateException("Failed to configure javax.xml DocumentBuilderFactory in Esthree Builder", exception);
		} catch (TransformerConfigurationException exception) {
			throw new IllegalStateException("Failed to configure javax.xml Transformer in Esthree Builder", exception);
		}
	}
}