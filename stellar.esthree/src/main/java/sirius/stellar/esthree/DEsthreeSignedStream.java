package sirius.stellar.esthree;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringJoiner;

import static java.lang.Math.min;
import static java.lang.System.arraycopy;
import static java.nio.charset.StandardCharsets.UTF_8;

/// Wraps an InputStream and signs it chunk by chunk using AWS Signature V4.
/// @see <a href="https://tiny.cc/aws_sigv4">AWS Reference</a>
final class DEsthreeSignedStream extends InputStream {

	private final InputStream source;
	private final DEsthreeSigner signer;
	private final String date;
	private final String region;
	private final byte[] signingKey;

	private byte[] previous;
	private byte[] buffer;
	private boolean finished;

	private int bufferPosition = 0;
	private int bufferLimit = 0;

	DEsthreeSignedStream(InputStream source, DEsthreeSigner signer, String date, String region, String candidate) {
		this.source = source;
		this.signer = signer;
		this.date = date;
		this.region = region;

		this.signingKey = signer.signingKey(date.substring(0, 8));
		this.previous = signer.hmac(this.signingKey, candidate);
		this.finished = false;

		this.buffer = new byte[0];
	}

	@Override
	public int read() throws IOException {
		if (this.bufferPosition >= this.bufferLimit && this.refillBuffer()) return -1;
		return this.buffer[this.bufferPosition++] & 0xFF;
	}

	@Override
	public int read(byte[] buffer, int offset, int length) throws IOException {
		int count = 0;
		while (length > 0) {
			if (this.bufferPosition >= this.bufferLimit && this.refillBuffer()) break;

			int copy = min(length, this.bufferLimit - this.bufferPosition);
			arraycopy(this.buffer, this.bufferPosition, buffer, offset, copy);

			this.bufferPosition += copy;
			offset += copy;
			length -= copy;
			count += copy;
		}
		return (count == 0) ? -1 : count;
	}

	@Override
	public int available() {
		return this.bufferLimit - this.bufferPosition;
	}

	/// Read some bytes from [#source] & allocate buffer, with space for chunk size & signature.
	/// @return `true` if EOF is reached, `false` for successful buffer refilling.
	private boolean refillBuffer() throws IOException {
		byte[] chunk = new byte[16 * 1024];
		if (this.finished) return (this.bufferPosition >= this.bufferLimit);

		int read = this.source.read(chunk);
		if (read == -1) {
			this.buffer = this.buildChunk(new byte[0]);
			this.bufferPosition = 0;
			this.bufferLimit = this.buffer.length;
			this.finished = true;
			return false;
		}

		byte[] actual = new byte[read];
		arraycopy(chunk, 0, actual, 0, read);

		this.buffer = this.buildChunk(actual);
		this.bufferPosition = 0;
		this.bufferLimit = this.buffer.length;
		return false;
	}

	/// Build a chunk from the provided read chunk, from the regular [InputStream].
	private byte[] buildChunk(byte[] payload) {
		String hash = this.signer.hex(this.signer.sha256(payload));

		String scope = this.date.substring(0, 8) + "/" + this.region + "/s3/aws4_request";
		String candidate = new StringJoiner("\n")
				.add("AWS4-HMAC-SHA256-PAYLOAD")
				.add(this.date)
				.add(scope)
				.add(this.signer.hex(this.previous))
				.add(hash)
				.toString();

		byte[] signature = this.signer.hmac(this.signingKey, candidate);
		this.previous = signature;

		String lengthHex = Integer.toHexString(payload.length);
		String signatureHex = this.signer.hex(signature);

		byte[] header = (lengthHex + ";chunk-signature=" + signatureHex + "\r\n").getBytes(UTF_8);
		byte[] footer = "\r\n".getBytes(UTF_8);

		byte[] result = new byte[header.length + payload.length + footer.length];
		arraycopy(header, 0, result, 0, header.length);
		arraycopy(payload, 0, result, header.length, payload.length);
		arraycopy(footer, 0, result, (header.length + payload.length), footer.length);

		return result;
	}

	/// Measure how large the size of the provided source (size of a stream),
	/// will be after signing that stream with this signer.
	///
	/// @implNote Magic values:
	/// - `chunkSize` is the chunk buffer size, in [#refillBuffer]
	/// - `prefixSize` is the chunk prefix size, `header` in [#buildChunk]
	/// - `signatureSize` is the size of a hexadecimal-encoded HMAC SHA-256
	/// - `terminalSize` is the size of the final chunk size number
	///   (hexadecimal-encoded), sent in [#refillBuffer]
	/// - `crlfSize` is the number of bytes in UTF-8 `\r\n`
	static long measure(long source) {
		int chunkSize = (16 * 1024);
		int prefixSize = ";chunk-signature=".getBytes(UTF_8).length;

		int signatureSize = 64;
		int terminalSize = 1;
		int crlfSize = "\r\n".getBytes(UTF_8).length;

		long total = 0;
		while (source > 0) {
			int payload = (int) min(chunkSize, source);

			total += Integer.toHexString(payload).getBytes(UTF_8).length;
			total += prefixSize + signatureSize + crlfSize;

			total += payload;
			total += crlfSize;

			source -= payload;
		}
		total += terminalSize;
		total += prefixSize + signatureSize + crlfSize;
		total += crlfSize;
		return total;
	}
}