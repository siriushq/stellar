package sirius.stellar.security.totp;

import sirius.stellar.serialization.base32.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

import static java.lang.ThreadLocal.withInitial;
import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.security.Security.getProviders;

/// Domain implementation of [Totp].
final class DTotp implements Totp {

	/// `java.security` standard name for [Mac] HMAC-SHA1 algorithm.
	private static final String MAC_ALGORITHM = "HmacSHA1";

	/// Security provider to use for creating instances of [Mac].
	private static final Provider MAC_PROVIDER =
			getProviders("Mac." + MAC_ALGORITHM)[0];

	private final TotpRandom random;
	private final TotpClock clock;
	private final long step;

	private final ThreadLocal<Mac> hmacSha1;

	DTotp(TotpRandom random, TotpClock clock, long step) {
		this.random = random;
		this.clock = clock;
		this.step = step;

		this.hmacSha1 = withInitial(this::acquireHmacSha1);
	}

	@Override
	public String secret() {
		byte[] buffer = new byte[10];
		this.random.next(buffer);

		return new String(Base32.encode(buffer));
	}

	@Override
	public boolean valid(String secret, int code) {
		long current = this.clock.epoch() / this.step;
		for (int i = -1; i <= 1; i++) {
			if (generate(secret, current + i) != code) continue;
			return true;
		}
		return false;
	}

	@Override
	public int code(String secret) {
		long current = this.clock.epoch() / this.step;
		return generate(secret, current);
	}

	@Override
	public long remaining() {
		return this.step - (this.clock.epoch() % this.step);
	}

	@Override
	public void release() {
		this.hmacSha1.remove();
	}

	/// Creates a [Mac] with the static [#PROVIDER] and [#MAC_ALGORITHM]
	/// algorithm, for thread-local instantiation.
	private Mac acquireHmacSha1() {
		try {
			return Mac.getInstance(MAC_ALGORITHM, MAC_PROVIDER);
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("Failed to obtain Mac", exception);
		}
	}

	/// Perform RFC 6238 TOTP generation using RFC 4226 dynamic truncation.
	/// @param secret Base32-encoded entropy used as the HMAC key.
	/// @param interval The discrete time-step counter.
	/// @return 6-digit code (in range 0 to 999999).
	private int generate(String secret, long interval) {
		try {
			byte[] key = Base32.decode(secret.toCharArray());
			byte[] data = ByteBuffer.allocate(8)
					.order(BIG_ENDIAN)
					.putLong(interval)
					.array();

			Mac mac = this.hmacSha1.get();
			mac.init(new SecretKeySpec(key, MAC_ALGORITHM));
			byte[] hmac = mac.doFinal(data);

			int offset = hmac[hmac.length - 1] & 0xF;
			int code = ((hmac[offset] & 0x7F) << 24)
				| ((hmac[offset + 1] & 0xFF) << 16)
				| ((hmac[offset + 2] & 0xFF) << 8)
				| (hmac[offset + 3] & 0xFF);

			return code % 1_000_000;
		} catch (InvalidKeyException exception) {
			throw new IllegalStateException(exception);
		}
	}
}