package sirius.stellar.security.totp;

import java.security.SecureRandom;
import java.util.Random;

/// Represents a random number generator for [Totp] purposes.
///
/// This is used to provide extensibility without `j.u.r.RandomGenerator`,
/// which is unavailable on older (<17) class libraries.
///
/// @see Totp.Builder#random
@FunctionalInterface
public interface TotpRandom {

	/// Generate random bytes and place them into the provided buffer.
	void next(byte[] buffer);

	/// Create an instance from the provided [Random].
	static TotpRandom of(Random random) {
		return random::nextBytes;
	}

	/// Create an instance using a new [SecureRandom].
	static TotpRandom secure() {
		return of(new SecureRandom());
	}
}