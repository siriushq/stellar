package sirius.stellar.security.totp;

import java.time.Clock;

/// Represents a clock for [Totp] purposes.
///
/// This is used to provide extensibility without depending on the
/// stateful [java.time.Clock] (which this is a reduced wrapper of).
///
/// @see Totp.Builder#clock
@FunctionalInterface
public interface TotpClock {

	/// Returns the time of this clock ("the current time") in epoch
	/// milliseconds ("since the beginning of time").
	long epoch();

	/// Create an instance from the provided [Clock].
	static TotpClock of(Clock clock) {
		return clock::millis;
	}

	/// Create an instance using the system clock.
	static TotpClock system() {
		return of(Clock.systemDefaultZone());
	}
}