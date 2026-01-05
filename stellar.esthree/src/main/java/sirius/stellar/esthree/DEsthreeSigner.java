package sirius.stellar.esthree;

import io.avaje.http.client.BodyContent;
import io.avaje.http.client.HttpClientRequest;
import org.jspecify.annotations.Nullable;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.StringJoiner;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.ZoneOffset.UTC;
import static java.util.Locale.US;

/// Domain implementation of [EsthreeSigner].
final class DEsthreeSigner implements EsthreeSigner {

	private final String accessKey;
	private final String secretKey;
	private final String region;

	private final DateTimeFormatter formatter;
	private final ThreadLocal<@Nullable MessageDigest> sha256;
	private final ThreadLocal<@Nullable Mac> hmacSha256;

	DEsthreeSigner(String accessKey, String secretKey, String region) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.region = region;

		this.sha256 = new ThreadLocal<>();
		this.hmacSha256 = new ThreadLocal<>();

		this.formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
				.withLocale(US)
				.withZone(UTC);
	}

	@Override
	public EsthreeSigner acquire() {
		try {
			if (this.sha256.get() != null && this.hmacSha256.get() != null) return this;

			this.sha256.set(MessageDigest.getInstance("SHA-256"));
			this.hmacSha256.set(Mac.getInstance("HmacSHA256"));
			return this;
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("Failed to obtain security provider for Esthree Signer", exception);
		}
	}

	@Override
	public void close() {
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
		MessageDigest sha256 = this.sha256.get();
		if (sha256 == null) throw new IllegalStateException();
		return sha256.digest(input);
	}

	/// Generate a HMAC with the provided key for the provided payload.
	byte[] hmac(byte[] key, String data) {
		try {
			Mac hmacSha256 = this.hmacSha256.get();
			if (hmacSha256 == null) throw new IllegalStateException();

			hmacSha256.init(new SecretKeySpec(key, "HmacSHA256"));
			return hmacSha256.doFinal(data.getBytes(UTF_8));
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