package sirius.stellar.logging.format;

import sirius.stellar.annotation.Internal;
import sirius.stellar.logging.Logger;

import java.util.Locale;
import java.util.ServiceLoader;

/// Represents a string formatter, used by the [Logger#format] public API.
/// This SPI allows for another implementation to be provided, if desired.
///
/// @see CombinedLoggerFormatter
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public interface LoggerFormatter {

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

	/// Obtain a [LoggerFormatter] instance, service-loading the first alternative
	/// implementation found on the class-path/module-path, if one is available.
	///
	/// @see Logger#format
	@Internal
	static LoggerFormatter create() {
		try {
			ServiceLoader<LoggerFormatter> loader = ServiceLoader.load(LoggerFormatter.class);
			for (LoggerFormatter formatter : loader) {
				if (formatter instanceof CombinedLoggerFormatter) continue;
				return formatter;
			}
			return new CombinedLoggerFormatter();
		} catch (Throwable throwable) {
			throw new IllegalStateException("Failed wiring alternate logger formatter", throwable);
		}
	}
}