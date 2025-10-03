package sirius.stellar.facility;

import org.jetbrains.annotations.Contract;

import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.random.RandomGenerator;

/**
 * Provides a facility for modifying and examining {@link String}s.
 * This class is entirely {@code null} safe and no operations should cause a {@link NullPointerException}.
 * However, methods may return {@code null}, causing a {@link NullPointerException} elsewhere.
 * <p>
 * The null safety of these methods strongly depends on the fact that invoking JDK {@link java.text.Format}
 * formatters and {@link String#format} uses the null-safe {@link String#valueOf(Object)} method to replace
 * arguments into the string, instead of attempting to invoke {@link Object#toString()} on a null argument.
 *
 * @since 1.0
 * @author Mechite
 */
public final class Strings {

	/**
	 * Represents an empty string.
	 * <p>
	 * This should be preferred over simply using an empty string literal when calling specific methods,
	 * though it could make less sense in concatenation scenarios. It guarantees to the reader that the
	 * string is indeed empty and not, e.g., a special whitespace character.
	 *
	 * @see Strings#SPACE
	 * @since 1.0
	 */
	public static final String EMPTY = "";

	/**
	 * Represents a single space ({@code " "}) character.
	 * <p>
	 * This should be preferred over simply using a string literal containing the space character when
	 * calling specific methods, though it could make less sense in concatenation scenarios. It guarantees
	 * to the reader that the string is indeed a single, regular space character, and not, e.g., a special,
	 * non-standard whitespace character.
	 *
	 * @see Strings#EMPTY
	 * @since 1.0
	 */
	public static final String SPACE = " ";

	/**
	 * Returns a formatted string using the provided format string and arguments.
	 * <p>
	 * This method invokes both {@link MessageFormat} and {@link String#format},
	 * allowing for both types of formatting to be applied to the message (as they
	 * do not clash with each other's syntax).
	 *
	 * @return The provided string, formatted, or null if the provided string is null,
	 * or the provided string if the argument array is null.
	 * @see Strings#format(Locale, String, Object...)
	 * @since 1.0
	 */
	@Contract(value = "null, _ -> null; _, null -> param1; !null, !null -> new", pure = true)
	public static String format(String string, Object... arguments) {
		if (string == null) return null;
		if (arguments == null) return string;

		return Optional.of(string)
				.map(MessageFormat::new)
				.map(format -> format.format(arguments))
				.map(message -> String.format(message, arguments))
				.orElseThrow(IllegalStateException::new);
	}

	/**
	 * Returns a formatted string using the provided format string and arguments.
	 * <p>
	 * This method invokes both {@link MessageFormat} and {@link String#format},
	 * allowing for both types of formatting to be applied to the message (as they
	 * do not clash with each other's syntax).
	 *
	 * @param locale Locale for {@link MessageFormat#MessageFormat(String, Locale)}
	 * and {@link String#format(Locale, String, Object...)} to accept. If null is
	 * provided, {@link Locale#ENGLISH} is used as a fallback.
	 * @return The provided string, formatted, or null if the provided string is null,
	 * or the provided string if the argument array is null.
	 * @see Strings#format(String, Object...)
	 * @since 1.0
	 */
	@Contract(value = "_, null, _ -> null; _, _, null -> param2; _, !null, !null -> new", pure = true)
	public static String format(Locale locale, String string, Object... arguments) {
		if (string == null) return null;
		if (arguments == null) return string;

		Locale effectiveLocale = (locale == null) ? Locale.ENGLISH : locale;
		return Optional.of(string)
				.map(pattern -> new MessageFormat(pattern, effectiveLocale))
				.map(format -> format.format(arguments))
				.map(message -> String.format(effectiveLocale, message, arguments))
				.orElseThrow(IllegalStateException::new);
	}

	/**
	 * Returns a shuffled string using the provided {@link Random} instance.
	 * <p>
	 * This method can be called with {@link SecureRandom} providing an alphabet string
	 * in order to generate a secure random identifier. If a very secure random generator
	 * is desired, {@link SecureRandom#getInstanceStrong()} can be used, however this is
	 * not recommended as {@link SecureRandom#SecureRandom()} is usually strong enough for
	 * most cases and this method could deplete the entropy pool of the operating system
	 * (less likely to happen on newer systems) depending on how often the random instance
	 * is used (how often this shuffle method is called), degrading quality/randomness.
	 *
	 * @see Strings#shuffle(RandomGenerator, char[])
	 * @since 1.0
	 */
	@Contract(value = "!null, !null -> new; null, !null -> param2; _, null -> null", pure = true)
	public static String shuffle(RandomGenerator random, String string) {
		if (string == null) return null;
		if (random == null) return string;

		return new String(shuffle(random, string.toCharArray()));
	}

	/**
	 * Returns a shuffled character array using the provided {@link Random} instance.
	 * <p>
	 * This method can be called with {@link SecureRandom} providing an alphabet array
	 * in order to generate a secure random identifier. If a very secure random generator
	 * is desired, {@link SecureRandom#getInstanceStrong()} can be used, however this is
	 * not recommended as {@link SecureRandom#SecureRandom()} is usually strong enough for
	 * most cases and this method could deplete the entropy pool of the operating system
	 * (less likely to happen on newer systems) depending on how often the random instance
	 * is used (how often this shuffle method is called), degrading quality/randomness.
	 *
	 * @see Strings#shuffle(RandomGenerator, String)
	 * @since 1.0
	 */
	@Contract(value = "!null, _ -> new; null, _ -> param2;", pure = true)
	public static char[] shuffle(RandomGenerator random, char[] characters) {
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