package sirius.stellar.logging.format;

import org.jspecify.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Optional;

/// Domain implementation of [Formatter].
final class DFormatter implements Formatter {

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