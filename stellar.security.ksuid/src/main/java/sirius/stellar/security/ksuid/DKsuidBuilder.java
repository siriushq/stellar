package sirius.stellar.security.ksuid;

import org.jspecify.annotations.Nullable;

/// Domain implementation of [Ksuid.Builder].
final class DKsuidBuilder implements Ksuid.Builder {

	@Nullable
	private KsuidRandom random;

	@Nullable
	private KsuidClock clock;

	@Override
	public Ksuid.Builder random(KsuidRandom random) {
		this.random = random;
		return this;
	}

	@Override
	public Ksuid.Builder clock(KsuidClock clock) {
		this.clock = clock;
		return this;
	}

	@Override
	public Ksuid build() {
		if (this.random == null) this.random = KsuidRandom.secure();
		if (this.clock == null) this.clock = KsuidClock.system();

		return new DKsuid(this.random, this.clock);
	}
}