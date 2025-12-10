package sirius.stellar.facility;

import org.jspecify.annotations.Nullable;
import sirius.stellar.annotation.Contract;

import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.random.RandomGenerator;

/// Provides a facility for modifying and examining [String]s.
/// This class is entirely `null` safe and no operations should cause a [NullPointerException].
/// However, methods may return `null`, causing a [NullPointerException] elsewhere.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class Strings {

	/// Represents an empty string.
	///
	/// This can be preferred over using an empty string literal, judged on a case-by-case basis,
	/// guaranteeing to the reader that the string is not, e.g., a special whitespace character.
	///
	/// @see Strings#SPACE
	/// @since 1.0
	public static final String EMPTY = "";

	/// Represents a single space (` `) character.
	///
	/// This can be preferred over using an empty string literal, judged on a case-by-case basis,
	/// guaranteeing to the reader that the string is not, e.g., a special whitespace character.
	///
	/// @see Strings#EMPTY
	/// @since 1.0
	public static final String SPACE = " ";

	/// Returns a shuffled string using the provided [RandomGenerator] instance.
	///
	/// @see SecureRandom#SecureRandom()
	/// @see Strings#shuffle(RandomGenerator, char[])
	/// @since 1.0
	@Nullable
	@Contract("!null, !null -> new; null, !null -> param2; _, null -> null")
	public static String shuffle(@Nullable RandomGenerator random, @Nullable String string) {
		if (string == null) return null;
		if (random == null) return string;

		return new String(shuffle(random, string.toCharArray()));
	}

	/// Returns a shuffled character array using the provided [RandomGenerator] instance.
	///
	/// @see SecureRandom#SecureRandom()
	/// @see Strings#shuffle(RandomGenerator, String)
	/// @since 1.0
	@Contract("!null, _ -> new; null, _ -> param2;")
	public static char[] shuffle(@Nullable RandomGenerator random, char[] characters) {
		char[] finalCharacters = Arrays.copyOf(characters, characters.length);
		if (random == null) return finalCharacters;
		for (int i = finalCharacters.length - 1; i > 0; i--) {
			int j = random.nextInt(i + 1);

			char previous = finalCharacters[i];
			finalCharacters[i] = finalCharacters[j];
			finalCharacters[j] = previous;
		}
		return finalCharacters;
	}
}