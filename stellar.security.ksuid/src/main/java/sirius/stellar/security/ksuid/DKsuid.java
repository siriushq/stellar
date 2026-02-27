package sirius.stellar.security.ksuid;

import sirius.stellar.serialization.base62.Base62;

/// Domain implementation of [Ksuid].
final class DKsuid implements Ksuid {

	/// Constant offset from epoch time to allow extensibility in the
	/// future by reducing the time from the standard 01/01/1970 epoch.
	private static final long OFFSET = 1400000000L;

	private final KsuidRandom random;
	private final KsuidClock clock;

	DKsuid(KsuidRandom random, KsuidClock clock) {
		this.random = random;
		this.clock = clock;
	}

	@Override
	public Identifier identifier() {
		byte[] bytes = new byte[20];
		this.random.next(bytes);

		long epoch = (this.clock.epoch() / 1000L) - OFFSET;
		bytes[0] = (byte) (epoch >> 24);
		bytes[1] = (byte) (epoch >> 16);
		bytes[2] = (byte) (epoch >> 8);
		bytes[3] = (byte) (epoch);

		char[] encoded = Base62.encode(bytes);
		return new DKsuidIdentifier(encoded);
	}

	@Override
	public Identifier identifier(String input) {
		if (input.length() < 27) throw new IllegalArgumentException("Short input provided");
		return new DKsuidIdentifier(input.toCharArray());
	}

	@Override
	public Identifier identifier(char[] input) {
		if (input.length < 27) throw new IllegalArgumentException("Short input provided");
		return new DKsuidIdentifier(input);
	}
}