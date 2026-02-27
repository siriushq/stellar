package sirius.stellar.security.ksuid;

import java.security.SecureRandom;
import java.util.Random;

/// Represents a random number generator for [Ksuid] purposes.
///
/// This is used to provide extensibility without `j.u.r.RandomGenerator`,
/// which is unavailable on older (<17) class libraries.
///
/// @see Ksuid.Builder#random
@FunctionalInterface
public interface KsuidRandom {

	/// Generate random bytes and place them into the provided buffer.
	void next(byte[] buffer);

	/// Create an instance from the provided [Random].
	static KsuidRandom of(Random random) {
		return random::nextBytes;
	}

	/// Create an instance using a new [SecureRandom].
	static KsuidRandom secure() {
		return of(new SecureRandom());
	}
}