package sirius.stellar.security.totp;

/// Domain implementation of [Totp].
final class DTotp implements Totp {

	private final TotpRandom random;
	private final TotpClock clock;
	private final long step;

	DTotp(TotpRandom random, TotpClock clock, long step) {
		this.random = random;
		this.clock = clock;
		this.step = step;
	}

	@Override
	public String secret() {
		byte[] buffer = new byte[10];
		this.random.next(buffer);
		return base32(buffer);
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

//private int generate(String secret, long interval) {
//	try {
//		byte[] key = decodeBase32(secret);
//		byte[] data = ByteBuffer.allocate(8).putLong(interval).array();
//
//		Mac mac = Mac.getInstance(ALGORITHM);
//		mac.init(new SecretKeySpec(key, ALGORITHM));
//		byte[] hash = mac.doFinal(data);
//
//		int offset = hash[hash.length - 1] & 0xF;
//		int binary = ((hash[offset] & 0x7F) << 24) |
//				((hash[offset + 1] & 0xFF) << 16) |
//				((hash[offset + 2] & 0xFF) << 8) |
//				(hash[offset + 3] & 0xFF);
//
//		return binary % 1_000_000;
//	} catch (GeneralSecurityException e) {
//		throw new IllegalStateException("HmacSHA1 not supported on this JVM", e);
//	}
//}
//
//private String base32(byte[] data) {
//	StringBuilder sb = new StringBuilder();
//	int buffer = 0, bitsLeft = 0;
//	for (byte b : data) {
//		buffer = (buffer << 8) | (b & 0xFF);
//		bitsLeft += 8;
//		while (bitsLeft >= 5) {
//			sb.append(ALPHABET.charAt((buffer >> (bitsLeft - 5)) & 0x1F));
//			bitsLeft -= 5;
//		}
//	}
//	if (bitsLeft > 0) sb.append(ALPHABET.charAt((buffer << (5 - bitsLeft)) & 0x1F));
//	return sb.toString();
//}
//
//private byte[] decodeBase32(String secret) {
//	byte[] out = new byte[(secret.length() * 5) / 8];
//	int buffer = 0, bitsLeft = 0, index = 0;
//	for (char c : secret.toCharArray()) {
//		int val = ALPHABET.indexOf(Character.toUpperCase(c));
//		if (val < 0) continue;
//		buffer = (buffer << 5) | val;
//		bitsLeft += 5;
//		if (bitsLeft >= 8) {
//			out[index++] = (byte) (buffer >> (bitsLeft - 8));
//			bitsLeft -= 8;
//		}
//	}
//	return out;
//}
}