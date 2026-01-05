package sirius.stellar.esthree;

import io.avaje.http.client.BodyContent;
import io.avaje.http.client.HttpClientRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.StringJoiner;

import static java.lang.ThreadLocal.withInitial;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.security.Security.getProviders;
import static java.time.ZoneOffset.UTC;
import static java.util.Locale.US;

/// Domain implementation of [EsthreeSigner].
final class DEsthreeSigner implements EsthreeSigner {

	/// `java.security` standard name for [MessageDigest] SHA256 algorithm.
	private static final String DIGEST_ALGORITHM = "SHA-256";

	/// `java.security` standard name for [Mac] HMAC-SHA256 algorithm.
	private static final String MAC_ALGORITHM = "HmacSHA256";

	/// Security provider to use for creating instances of [MessageDigest].
	private static final Provider DIGEST_PROVIDER =
			getProviders("MessageDigest." + DIGEST_ALGORITHM)[0];

	/// Security provider to use for creating instances of [Mac].
	private static final Provider MAC_PROVIDER =
			getProviders("Mac." + MAC_ALGORITHM)[0];

	private final String accessKey;
	private final String secretKey;
	private final String region;

	private final DateTimeFormatter formatter;
	private final ThreadLocal<MessageDigest> sha256;
	private final ThreadLocal<Mac> hmacSha256;

	DEsthreeSigner(String accessKey, String secretKey, String region) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.region = region;

		this.sha256 = withInitial(this::acquireSha256);
		this.hmacSha256 = withInitial(this::acquireHmacSha256);

		this.formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
				.withLocale(US)
				.withZone(UTC);
	}

	/// Creates a [MessageDigest] with the static [#PROVIDER] and [#SHA_256]
	/// algorithm, for thread-local instantiation.
	private MessageDigest acquireSha256() {
		try {
			return MessageDigest.getInstance(DIGEST_ALGORITHM, DIGEST_PROVIDER);
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("Failed to obtain `MessageDigest` for Esthree Signer", exception);
		}
	}

	/// Creates a [Mac] with the static [#PROVIDER] and [#HMAC_SHA_256]
	/// algorithm, for thread-local instantiation.
	private Mac acquireHmacSha256() {
		try {
			return Mac.getInstance(MAC_ALGORITHM, MAC_PROVIDER);
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("Failed to obtain `Mac` for Esthree Signer", exception);
		}
	}

	@Override
	public void release() {
		this.sha256.remove();
		this.hmacSha256.remove();
	}

	@Override
	public void sign(String method, HttpClientRequest request, BodyContent body) {
		String hash = hex(sha256(body.content()));
		String now = this.formatter.format(Instant.now());
		this.sign(method, request, hash, now);
	}

	@Override
	public InputStream sign(String method, HttpClientRequest request, InputStream stream) {
		String hash = "STREAMING-AWS4-HMAC-SHA256-PAYLOAD";
		String now = this.formatter.format(Instant.now());

		String candidate = this.sign(method, request, hash, now);
		return new DEsthreeSignedStream(stream, this, now, this.region, candidate);
	}

	/// Sign the provided request with the provided payload hash & date.
	/// Returns the "string-to-sign" of canonical request, if needed for streaming.
	String sign(String method, HttpClientRequest request, String hash, String now) {
		String date = now.substring(0, 8);

		URI uri = URI.create(request.url());
		int port = uri.getPort();
		String host = uri.getHost() + (port != -1 ? (":" + port) : "");

		request.header("x-amz-content-sha256", hash);
		request.header("x-amz-date", now);
		String headers = "host;x-amz-content-sha256;x-amz-date";

		String canonical = new StringJoiner("\n")
				.add(method)
				.add(uri.getPath().isEmpty() ? "/" : uri.getPath())
				.add(uri.getRawQuery() == null ? "" : this.query(uri.getRawQuery()))
				.add("host:" + host)
				.add("x-amz-content-sha256:" + hash)
				.add("x-amz-date:" + now)
				.add("")
				.add(headers)
				.add(hash)
				.toString();
		String canonicalHash = hex(sha256(canonical.getBytes(UTF_8)));

		String scope = date + "/" + this.region + "/s3/aws4_request";
		String candidate = new StringJoiner("\n")
				.add("AWS4-HMAC-SHA256")
				.add(now)
				.add(scope)
				.add(canonicalHash)
				.toString();

		byte[] key = signingKey(date);
		String signature = hex(hmac(key, candidate));

		String header = new StringJoiner(", ", "AWS4-HMAC-SHA256 ", "")
				.add("Credential=" + this.accessKey + "/" + scope)
				.add("SignedHeaders=" + headers)
				.add("Signature=" + signature)
				.toString();
		request.header("Authorization", header);

		return candidate;
	}

	/// Creates a signing key from the provided date.
	byte[] signingKey(String date) {
		byte[] dateKey = this.hmac(("AWS4" + this.secretKey).getBytes(UTF_8), date);
		byte[] regionKey = this.hmac(dateKey, this.region);
		byte[] serviceKey = this.hmac(regionKey, "s3");
		return this.hmac(serviceKey, "aws4_request");
	}

	/// Generate a SHA256 digest for the provided input.
	byte[] sha256(byte[] input) {
		return this.sha256.get().digest(input);
	}

	/// Generate a HMAC with the provided key for the provided payload.
	byte[] hmac(byte[] key, String data) {
		try {
			Mac mac = this.hmacSha256.get();
			mac.init(new SecretKeySpec(key, "HmacSHA256"));
			return mac.doFinal(data.getBytes(UTF_8));
		} catch (InvalidKeyException exception) {
			throw new IllegalStateException("Invalid key during HMAC SHA256 signing in Esthree Signer", exception);
		}
	}

	/// Convert the provided `byte[]` to a hexadecimal representation.
	/// Used instead of `java.util.HexFormat` to support older JVMs.
	String hex(byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		for (byte b : bytes) builder.append(String.format("%02x", b));
		return builder.toString();
	}

	/// Returns a sorted query parameter string for the provided one,
	/// to be retrieved from [URI#getRawQuery()].
	String query(String query) {
		String[] parameters = query.split("&");
		Arrays.sort(parameters, (first, second) -> {
			first = first.split("=")[0];
			second = second.split("=")[0];
			return first.compareTo(second);
		});
		return String.join("&", parameters);
	}
}