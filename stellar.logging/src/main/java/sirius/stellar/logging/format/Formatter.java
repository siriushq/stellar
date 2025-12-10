package sirius.stellar.logging.format;

import org.jspecify.annotations.NullUnmarked;
import sirius.stellar.annotation.Contract;
import sirius.stellar.logging.Logger;

import java.util.Locale;
import java.util.ServiceLoader;

/// Represents a string formatter, used by the [Logger#format] public API.
/// This allows for another implementation to be provided, if desired.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
@NullUnmarked
public interface Formatter {

	/// Returns the provided string, formatted, or `null` if the provided string
	/// is `null`, or if the argument array is `null`.
	///
	/// @see #formatString(Locale, String, Object...)
	/// @since 1.0
	@Contract("null, _ -> null; _, null -> param1; !null, !null -> new")
	String formatString(String string, Object[] arguments);

	/// Returns the provided string, formatted, or `null` if the provided string
	/// is `null`, or if the argument array is `null` (using the provided locale
	/// for formatting).
	///
	/// @see #formatString(String, Object...)
	/// @since 1.0
	@Contract("_, null, _ -> null; _, _, null -> param2; _, !null, !null -> new")
	String formatString(Locale locale, String string, Object[] arguments);

	/// Obtain a [Formatter] instance, service-loading the first alternative
	/// implementation found, if one is available.
	static Formatter create() {
		try {
			ServiceLoader<Formatter> loader = ServiceLoader.load(Formatter.class);
			for (Formatter formatter : loader) {
				if (formatter instanceof DFormatter) continue;
				return formatter;
			}
			return new DFormatter();
		} catch (Throwable throwable) {
			throw new IllegalStateException("Failed wiring alternate logger formatter", throwable);
		}
	}
}