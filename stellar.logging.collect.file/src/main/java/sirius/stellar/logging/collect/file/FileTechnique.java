package sirius.stellar.logging.collect.file;

import sirius.stellar.logging.LoggerMessage;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneOffset.UTC;
import static java.util.Locale.US;

/// SPI (Service Provider Interface) for replacing the output format
/// (the "technique") of [FileCollector].
///
/// @since 1.0
public interface FileTechnique {

	/// Return a writable [String] for the provided message.
	String format(LoggerMessage message);

	/// Return a file extension suffix, e.g. `.txt`, for this technique.
	String extension();

	/// Return a header for the file, if applicable (e.g. for CSV format).
	default String header() {
		return "";
	}

	/// Return the path to log to, by default `./logs/...`.
	default Path directory() {
		return Path.of("logging");
	}
}

/// Default technique (for human-readable, colored logging, to `stderr`).
final class PlainFileTechnique implements FileTechnique {

	private final DateTimeFormatter formatter;

	PlainFileTechnique() {
		this.formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
				.withLocale(US)
				.withZone(UTC);
	}

	@Override
	public String extension() {
		return ".txt";
	}

	@Override
	public String format(LoggerMessage message) {
		StringBuilder builder = new StringBuilder(128);
		builder.append("[");
		builder.append(this.formatter.format(message.time()));
		builder.append(" | ");
		builder.append(message.level().display());
		builder.append(" | ");
		builder.append(message.thread());
		builder.append(" | ");
		builder.append(message.name());
		builder.append("] ");
		builder.append(message.text());
		return builder.toString();
	}
}