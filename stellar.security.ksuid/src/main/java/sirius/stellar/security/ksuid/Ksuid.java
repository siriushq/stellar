package sirius.stellar.security.ksuid;

import sirius.stellar.serialization.base62.Base62;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.Random;
import java.util.ServiceLoader;

import static java.util.ServiceLoader.load;

/// Service client for performing KSUID (K-Sortable Globally
/// Unique Identifier) generation.
///
/// Generators are created using the static [#builder()] method.
/// The default implementation of this client is fully thread-safe.
///
/// {@snippet lang = "java":
/// Ksuid ksuid = Ksuid.builder()
///         .random(new SecureRandom())
///         .build();
/// println(ksuid.identifier());
/// }
public interface Ksuid {

	/// Generates a new random identifier. All implementations must
	/// delegate [Object#toString] to the [Identifier#string] method.
	Identifier identifier();

	/// Parses the provided string input as an identifier.
	/// @throws IllegalArgumentException short input provided
	Identifier identifier(String input);

	/// Parses the provided character array input as an identifier.
	/// @throws IllegalArgumentException short input provided
	Identifier identifier(char[] input);

	/// Return a builder to construct [Ksuid] generator instances with.
	static Builder builder() {
		try {
			ServiceLoader<Builder> loader = load(Builder.class);
			for (Builder builder : loader) {
				if (builder instanceof DKsuidBuilder) continue;
				return builder;
			}
			return new DKsuidBuilder();
		} catch (Throwable throwable) {
			throw new IllegalStateException("Failed wiring alternate KSUID implementation", throwable);
		}
	}

	/// @see Ksuid
	interface Identifier extends CharSequence {

		/// Returns this identifier formatted as a [Base62] unique string.
		String string();

		/// Returns this identifier formatted as a [Base62] character array.
		char[] value();

		@Override
		default int length() {
			return this.string().length();
		}

		@Override
		default char charAt(int index) {
			return this.string().charAt(index);
		}

		@Override
		default CharSequence subSequence(int start, int end) {
			return this.string().subSequence(start, end);
		}
	}

	/// @see Ksuid
	interface Builder {

		/// Supply a [Random] generator instance.
		/// The default is derived from [SecureRandom].
		///
		/// This method is a delegate of [#random(KsuidRandom)] using
		/// [KsuidRandom#of(Random)] to use the provided instance.
		default Builder random(Random random) {
			return this.random(KsuidRandom.of(random));
		}

		/// Supply a [KsuidRandom] generator instance.
		/// @see #random(Random)
		Builder random(KsuidRandom random);

		/// Supply a different [Clock] instance.
		/// The default is derived from [Clock#systemDefaultZone].
		///
		/// This method is a delegate of [#clock(KsuidClock)] using
		/// [KsuidClock#of(Clock)] to use the provided instance.
		default Builder clock(Clock clock) {
			return this.clock(KsuidClock.of(clock));
		}

		/// Supply a [KsuidClock] instance.
		/// @see #clock(Clock)
		Builder clock(KsuidClock clock);

		/// Build and return the client (which is an [AutoCloseable]).
		Ksuid build();
	}
}