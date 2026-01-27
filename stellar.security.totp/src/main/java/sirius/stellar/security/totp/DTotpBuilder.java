package sirius.stellar.security.totp;

import org.jspecify.annotations.Nullable;

import java.time.Clock;

/// Domain implementation of [Totp.Builder].
final class DTotpBuilder implements Totp.Builder {

	@Nullable
	private TotpRandom random;

	@Nullable
	private TotpClock clock;

	private long step;

	@Override
	public Totp.Builder random(TotpRandom random) {
		this.random = random;
		return this;
	}

	@Override
	public Totp.Builder clock(TotpClock clock) {
		this.clock = clock;
		return this;
	}

	@Override
	public Totp.Builder step(long step) {
		this.step = step;
		return this;
	}

	@Override
	public Totp build() {
		if (this.random == null) this.random = TotpRandom.secure();
		if (this.clock == null) this.clock = TotpClock.system();
		if (this.step <= 0) this.step = 30_000;

		return new DTotp(this.random, this.clock, this.step);
	}
}