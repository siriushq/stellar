package sirius.stellar.logging;

import org.jspecify.annotations.Nullable;
import sirius.stellar.annotation.Contract;
import sirius.stellar.logging.dispatch.Dispatcher;
import sirius.stellar.logging.format.Formatter;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ServiceLoader;

/// Provides a statically-accessible manner of formatting strings.
///
/// Under the hood, this uses both [MessageFormat] and [String#format] to format
/// logging messages in [Dispatcher] implementations and the [Logger] public API
/// to allow for non-clashing syntax (both ignore extra arguments) to be used.
///
/// Another implementation can be provided using the [Formatter] service
/// provider interface, if desired, and will replace all methods here.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class LoggerFormat implements Formatter {

	/// The formatter which the static methods of [LoggerFormat] are to use, by
	/// default, the [LoggerFormat] implementation itself given no alternative.
	static Formatter delegate = new LoggerFormat();

	static {
		try {
			ServiceLoader<Formatter> loader = ServiceLoader.load(Formatter.class);
			for (Formatter formatter : loader) {
				if (formatter instanceof LoggerFormat) continue;
				delegate = formatter;
			}
		} catch (Throwable throwable) {
			throw new IllegalStateException("Failed wiring alternate logger formatter", throwable);
		}
	}

	/// Returns the provided string, formatted, or `null` if the provided string
	/// is `null`, or if the argument array is `null`.
	///
	/// @see LoggerFormat
	/// @see #format(Locale, String, Object...)
	/// @since 1.0
	@Nullable
	@Contract("null, _ -> null; _, null -> param1; !null, !null -> new")
	public static String format(@Nullable String string, Object @Nullable ... arguments) {
		return delegate.formatString(string, arguments);
	}

	/// Returns the provided string, formatted, or `null` if the provided string
	/// is `null`, or if the argument array is `null`.
	///
	/// @param locale Locale for [MessageFormat#MessageFormat(String,Locale)] and
	/// [String#format(Locale,String,Object...)] to accept. If null is provided,
	/// [Locale#ENGLISH] is used as a fallback.
	///
	/// A different implementation of [Formatter] being used by [LoggerFormat]
	/// will cause this argument to be interpreted differently.
	///
	/// @see LoggerFormat
	/// @see #format(String, Object...)
	/// @since 1.0
	@Nullable
	@Contract("_, null, _ -> null; _, _, null -> param2; _, !null, !null -> new")
	public static String format(@Nullable Locale locale, @Nullable String string, Object @Nullable ... arguments) {
		return delegate.formatString(locale, string, arguments);
	}

	@Override
	public @Nullable String formatString(@Nullable String string, Object @Nullable ... arguments) {
		if (string == null) return null;
		if (arguments == null) return string;

		return Optional.of(string)
				.map(MessageFormat::new)
				.map(format -> format.format(arguments))
				.map(message -> String.format(message, arguments))
				.orElseThrow(IllegalStateException::new);
	}

	@Override
	public @Nullable String formatString(@Nullable Locale locale, @Nullable String string, Object @Nullable ... arguments) {
		if (string == null) return null;
		if (arguments == null) return string;

		Locale effectiveLocale = (locale == null) ? Locale.ENGLISH : locale;
		return Optional.of(string)
				.map(pattern -> new MessageFormat(pattern, effectiveLocale))
				.map(format -> format.format(arguments))
				.map(message -> String.format(effectiveLocale, message, arguments))
				.orElseThrow(IllegalStateException::new);
	}
}