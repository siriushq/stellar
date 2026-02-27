package sirius.stellar.security.ksuid;

import java.time.Clock;

/// Represents a clock for [Ksuid] purposes.
///
/// This is used to provide extensibility without depending on the
/// stateful [java.time.Clock] (which this is a reduced wrapper of).
///
/// @see Ksuid.Builder#clock
@FunctionalInterface
public interface KsuidClock {

	/// Returns the time of this clock ("the current time") in epoch
	/// milliseconds ("since the beginning of time").
	long epoch();

	/// Create an instance from the provided [Clock].
	static KsuidClock of(Clock clock) {
		return clock::millis;
	}

	/// Create an instance using the system clock.
	static KsuidClock system() {
		return of(Clock.systemDefaultZone());
	}
}