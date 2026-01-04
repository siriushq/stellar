package sirius.stellar.logging.format;

import java.text.MessageFormat;
import java.util.Formatter;
import java.util.IllegalFormatException;
import java.util.Locale;

import static java.util.Locale.Category.FORMAT;

/// The default implementation of [LoggerFormatter], combining an invocation of
/// both [String#format] and [MessageFormat], in order to allow both with their
/// non-clashing syntax, for interpolation, as efficiently as possible.
final class CombinedLoggerFormatter implements LoggerFormatter {

	@Override
	public String formatString(String string, Object[] arguments) {
		return this.formatString(Locale.getDefault(FORMAT), string, arguments);
	}

	@Override
	public String formatString(Locale locale, String string, Object[] arguments) {
		if (arguments.length == 0) return string;
		string = messageFormat(locale, string, arguments);
		string = stringFormat(locale, string, arguments);
		return string;
	}

	/// Conditionally invokes [MessageFormat] on the provided string, with the
	/// provided arguments, only if it actually matches the required format,
	/// otherwise returning the format string without failure.
	///
	/// @implNote A regular expression, e.g. `string.matches("\\{\\d")`, could
	/// be used to perform this same check, but the cost of this is ~15% higher.
	private static String messageFormat(Locale locale, String string, Object[] arguments) {
		boolean matches = false;
		for (int i = 0; i < string.length() - 2; i++) {
			char parenthesis = string.charAt(i);
			if (parenthesis != '{') continue;

			char digit = string.charAt(i + 1);
			if (digit < '0' || digit > '9') continue;

			matches = true;
			break;
		}

		try {
			if (!matches) return string;

			MessageFormat formatter = new MessageFormat(string, locale);
			return formatter.format(arguments);
		} catch (IllegalArgumentException exception) {
			return string;
		}
	}

	/// Conditionally invokes [Formatter] (same as the [String#format] method)
	/// on the provided string, with the provided arguments, only if it actually
	/// matches the required format, otherwise returning the format string
	/// without failure.
	///
	/// @implNote The check for format-string syntax is very basic, only making
	/// the assertion that a `%` is in the string, and allowing illegal formats
	/// to be ignored and the format string safely returned.
	private static String stringFormat(Locale locale, String string, Object[] arguments) {
		if (string.indexOf('%') == -1) return string;

		try {
			Formatter formatter = new Formatter(locale);
			return formatter.format(string, arguments).toString();
		} catch (IllegalFormatException exception) {
			return string;
		}
	}
}