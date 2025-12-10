package sirius.stellar.logging.format;

import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import sirius.stellar.annotation.Contract;
import sirius.stellar.logging.LoggerFormat;

import java.util.Locale;

/// Represents a string formatter, used by the [LoggerFormat] public API.
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
}