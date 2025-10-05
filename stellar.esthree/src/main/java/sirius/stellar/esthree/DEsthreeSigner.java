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
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;
import java.util.concurrent.locks.ReentrantLock;

import static java.nio.charset.StandardCharsets.*;
import static java.time.ZoneOffset.*;

/// Default/domain implementation of [EsthreeSigner].
final class DEsthreeSigner implements EsthreeSigner {

	private final String accessKey;
	private final String secretKey;
	private final String region;

	private final DateTimeFormatter formatter;
	private final MessageDigest sha256;
	private final Mac hmacSha256;

	private final ReentrantLock lockSha256;
	private final ReentrantLock lockHmacSha256;

	DEsthreeSigner(String accessKey, String secretKey, String region) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.region = region;

		try {
			this.formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(UTC);
			this.sha256 = MessageDigest.getInstance("SHA-256");
			this.hmacSha256 = Mac.getInstance("HmacSHA256");

			this.lockSha256 = new ReentrantLock();
			this.lockHmacSha256 = new ReentrantLock();
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("Failed to obtain security provider for Esthree Signer", exception);
		}
	}

	@Override
	public void sign(HttpClientRequest request, BodyContent body) {
		String hash = hex(sha256(body.content()));
		this.sign(request, hash, Instant.now());
	}

	@Override
	public InputStream sign(HttpClientRequest request, InputStream stream) {
		String hash = "STREAMING-AWS4-HMAC-SHA256-PAYLOAD";
		Instant instant = Instant.now();

		String candidate = this.sign(request, hash, instant);
		return new DEsthreeSignedStream(stream, this, this.formatter.format(instant), this.region, candidate);
	}

	/**
	 * Sign the provided request with the provided payload hash & date.
	 * Returns the "string-to-sign" of canonical request, if needed for streaming.
	 */
	String sign(HttpClientRequest request, String hash, Instant instant) {
		String now = this.formatter.format(instant);
		String date = now.substring(0, 8);

		URI uri = URI.create(request.url());
		String host = uri.getHost();

		request.header("Host", host);
		request.header("x-amz-date", now);
		request.header("x-amz-content-sha256", hash);
		String headers = "host;x-amz-content-sha256;x-amz-date";

		String canonical = new StringJoiner("\n")
				.add(request.method())
				.add(uri.getPath().isEmpty() ? "/" : uri.getPath())
				.add(uri.getQuery() == null ? "" : uri.getQuery())
				.add("host:" + host)
				.add("x-amz-content-sha256" + hash)
				.add("x-amz-date:" + now)
				.add("\n")
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

		String header = new StringJoiner(", ", "AWS4-HMAC-SHA256", "")
				.add("Credential=" + this.accessKey + "/" + scope)
				.add("SignedHeaders=" + headers)
				.add("Signature=" + signature)
				.toString();
		request.header("Authorization", header);
		return candidate;
	}

	/** Creates a signing key from the provided date. */
	byte[] signingKey(String date) {
		byte[] dateKey = this.hmac(("AWS4" + this.secretKey).getBytes(UTF_8), date);
		byte[] regionKey = this.hmac(dateKey, this.region);
		byte[] serviceKey = this.hmac(regionKey, "s3");
		return this.hmac(serviceKey, "aws4_request");
	}

	/** Generate a SHA256 digest for the provided input. */
	byte[] sha256(byte[] input) {
		try {
			this.lockSha256.lock();
			return this.sha256.digest(input);
		} finally {
			this.lockSha256.unlock();
		}
	}

	/** Generate a HMAC with the provided key for the provided payload. */
	byte[] hmac(byte[] key, String data) {
		try {
			this.lockHmacSha256.lock();
			this.hmacSha256.init(new SecretKeySpec(key, "HmacSHA256"));
			return this.hmacSha256.doFinal(data.getBytes(UTF_8));
		} catch (InvalidKeyException exception) {
			throw new IllegalStateException("Invalid key during HMAC SHA256 signing in Esthree Signer", exception);
		} finally {
			this.lockHmacSha256.unlock();
		}
	}

	/**
	 * Convert the provided {@code byte[]} to a hexadecimal representation.
	 * Used instead of {@code java.util.HexFormat} to support older JVMs.
	 */
	String hex(byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		for (byte b : bytes) builder.append(String.format("%02x", b));
		return builder.toString();
	}
}