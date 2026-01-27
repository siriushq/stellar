package sirius.stellar.security.totp;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.Random;
import java.util.ServiceLoader;

import static java.util.ServiceLoader.load;

/// Service client for performing RFC 6238 TOTP secret generation, and
/// (against a secret) code validation (for server applications) and
/// code generation (for implementing authenticator applications).
///
/// Essentially, [#secret] & [#valid] tend to be used in server
/// applications and [#code] & [#remaining] by authenticators.
///
/// This can be created using the static [#builder()] method.
/// Relinquish acquired resources with [#close()].
///
/// The default implementation of this client is fully thread-safe
/// and also accounts for network latency / clock drift.
///
/// {@snippet lang="java":
/// Totp totp = Totp.builder()
///         .random(new SecureRandom())
///         .build();
/// println(totp.secret());
/// }
///
/// @see sirius.stellar.esthree
public interface Totp {

	/// Generate a Base32-encoded secret.
	String secret();

	/// Validate the provided code, against a Base32-encoded secret.
	boolean valid(String secret, int code);

	/// Generate a 6-digit TOTP code from a Base32-encoded secret.
	int code(String secret);

	/// Returns in milliseconds how long a generated [#code] on the
	/// current clock has remaining.
	long remaining();

	/// Return a builder to construct [Totp] instances with.
	static Builder builder() {
		try {
			ServiceLoader<Builder> loader = load(Builder.class);
			for (Builder builder : loader) {
				if (builder instanceof DTotpBuilder) continue;
				return builder;
			}
			return new DTotpBuilder();
		} catch (Throwable throwable) {
			throw new IllegalStateException("Failed wiring alternate TOTP implementation", throwable);
		}
	}

	/// @see Totp
	interface Builder {

		/// Supply a [Random] generator instance.
		/// The default is derived from [SecureRandom].
		///
		/// This method is a delegate of [#random(TotpRandom)] using
		/// [TotpRandom#of(Random)] to use the provided instance.
		default Builder random(Random random) {
			return this.random(TotpRandom.of(random));
		}

		/// Supply a [TotpRandom] generator instance.
		/// @see #random(Random)
		Builder random(TotpRandom random);

		/// Supply a different [Clock] instance.
		/// The default is derived from [Clock#systemDefaultZone].
		///
		/// This method is a delegate of [#clock(TotpClock)] using
		/// [TotpClock#of(Clock)] to use the provided instance.
		default Builder clock(Clock clock) {
			return this.clock(TotpClock.of(clock));
		}

		/// Supply a [TotpClock] instance.
		/// @see #clock(Clock)
		Builder clock(TotpClock clock);

		/// Supply a different step interval (in milliseconds).
		/// The default is 30 seconds.
		Builder step(long step);

		/// Build and return the client (which is an [AutoCloseable]).
		Totp build();
	}
}