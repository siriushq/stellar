package sirius.stellar.facility;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
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

	/// Returns a formatted string using the provided format string and arguments.
	///
	/// This method invokes both [MessageFormat] and [String#format], allowing for
	/// both types of formatting to be applied to the message (as they do not clash
	/// with each other's syntax - both can ignore extra arguments).
	///
	/// @return The provided string, formatted, or `null` if the provided string is `null`,
	/// or the provided string, if the argument array is `null`.
	///
	/// @see Strings#format(Locale, String, Object...)
	/// @since 1.0
	@Nullable
	@Contract(value = "null, _ -> null; _, null -> param1; !null, !null -> new", pure = true)
	public static String format(@Nullable String string, Object @Nullable... arguments) {
		if (string == null) return null;
		if (arguments == null) return string;

		return Optional.of(string)
				.map(MessageFormat::new)
				.map(format -> format.format(arguments))
				.map(message -> String.format(message, arguments))
				.orElseThrow(IllegalStateException::new);
	}

	/// Returns a formatted string using the provided format string and arguments.
	///
	/// This method invokes both [MessageFormat] and [#format], allowing for both types
	/// of formatting to be applied to the message (as they do not clash with each
	/// other's syntax).
	///
	/// @param locale Locale for [MessageFormat#MessageFormat(String,Locale)] and
	/// [String#format(Locale,String,Object...)] to accept. If null is provided,
	/// [Locale#ENGLISH] is used as a fallback.
	///
	/// @return The provided string, formatted, or `null` if the provided string is `null`,
	/// or the provided string, if the argument array is `null`.
	///
	/// @see Strings#format(String, Object...)
	/// @since 1.0
	@Nullable
	@Contract(value = "_, null, _ -> null; _, _, null -> param2; _, !null, !null -> new", pure = true)
	public static String format(@Nullable Locale locale, @Nullable String string, Object @Nullable... arguments) {
		if (string == null) return null;
		if (arguments == null) return string;

		Locale effectiveLocale = (locale == null) ? Locale.ENGLISH : locale;
		return Optional.of(string)
				.map(pattern -> new MessageFormat(pattern, effectiveLocale))
				.map(format -> format.format(arguments))
				.map(message -> String.format(effectiveLocale, message, arguments))
				.orElseThrow(IllegalStateException::new);
	}

	/// Returns a shuffled string using the provided [RandomGenerator] instance.
	///
	/// @see SecureRandom#SecureRandom()
	/// @see Strings#shuffle(RandomGenerator, char[])
	/// @since 1.0
	@Nullable
	@Contract(value = "!null, !null -> new; null, !null -> param2; _, null -> null", pure = true)
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
	@Contract(value = "!null, _ -> new; null, _ -> param2;", pure = true)
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