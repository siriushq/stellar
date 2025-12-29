package sirius.stellar.logging.format;

import sirius.stellar.annotation.Internal;
import sirius.stellar.logging.Logger;

import java.util.Locale;
import java.util.ServiceLoader;

/// Represents a string formatter, used by the [Logger#format] public API.
/// This SPI allows for another implementation to be provided, if desired.
///
/// @see CombinedLogFormatter
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public interface LogFormatter {

	/// Returns the provided string, formatted.
	/// Implementations of this method should never `throw`.
	///
	/// @see #formatString(Locale, String, Object...)
	/// @since 1.0
	String formatString(String string, Object[] arguments);

	/// Returns the provided string, formatted (using the provided locale).
	/// Implementations of this method should never `throw`.
	///
	/// @see #formatString(String, Object...)
	/// @since 1.0
	String formatString(Locale locale, String string, Object[] arguments);

	/// Obtain a [LogFormatter] instance, service-loading the first alternative
	/// implementation found on the class-path/module-path, if one is available.
	///
	/// @see Logger#format
	@Internal
	static LogFormatter create() {
		try {
			ServiceLoader<LogFormatter> loader = ServiceLoader.load(LogFormatter.class);
			for (LogFormatter formatter : loader) {
				if (formatter instanceof CombinedLogFormatter) continue;
				return formatter;
			}
			return new CombinedLogFormatter();
		} catch (Throwable throwable) {
			throw new IllegalStateException("Failed wiring alternate logger formatter", throwable);
		}
	}
}