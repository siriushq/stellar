package sirius.stellar.logging.collect.console;

import sirius.stellar.logging.LoggerLevel;
import sirius.stellar.logging.LoggerMessage;

import java.io.PrintStream;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneOffset.UTC;
import static java.util.Locale.US;
import static sirius.stellar.facility.terminal.TerminalColor.*;

/// SPI (Service Provider Interface) for replacing the output destination and
/// format (the "technique") of [ConsoleCollector].
///
/// @since 1.0
public interface ConsoleTechnique {

	/// Return a displayable [String] for the provided message.
	String format(LoggerMessage message);

	/// Return a destination to log to, e.g. [System#err].
	///
	/// This does not prevent [ConsoleCollector] from overriding both
	/// [System#out] and [System#err] as dispatchers, but it does run
	/// before this overriding occurs.
	PrintStream destination();
}

/// Default technique (for human-readable, colored logging, to `stderr`).
final class HumanConsoleTechnique implements ConsoleTechnique {

	private final PrintStream stream;
	private final DateTimeFormatter formatter;

	HumanConsoleTechnique() {
		this.stream = System.err;
		this.formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
				.withLocale(US)
				.withZone(UTC);
	}

	@Override
	public PrintStream destination() {
		return this.stream;
	}

	@Override
	public String format(LoggerMessage message) {
		StringBuilder builder = new StringBuilder(128);

		builder.append(BLACK.foreground().bright());
		builder.append("[");

		builder.append(WHITE.foreground().dark());
		builder.append(this.formatter.format(message.time()));

		builder.append(BLACK.foreground().bright());
		builder.append(" | ");

		builder.append(display(message.level()));
		builder.append(BLACK.foreground().bright());
		builder.append(" | ");

		builder.append(WHITE.foreground().dark());
		builder.append(message.thread());

		builder.append(BLACK.foreground().bright());
		builder.append(" | ");

		builder.append(WHITE.foreground().dark());
		builder.append(message.name());

		builder.append(BLACK.foreground().bright());
		builder.append("] ");

		builder.append(WHITE.foreground().bright());
		builder.append(message.text());
		builder.append(DEFAULT.foreground().bright());

		return builder.toString();
	}

	/// Returns [LoggerLevel#display()] with a suitable color escape code
	/// prepended to the string depending on the logging level.
	private String display(LoggerLevel level) {
		String display = level.display();
		return switch (level) {
			case INFORMATION -> BLUE.foreground().bright() + display;
			case WARNING -> YELLOW.foreground().bright() + display;
			case ERROR, TRACING -> RED.foreground().bright() + display;
			case DIAGNOSIS, CONFIGURATION -> MAGENTA.foreground().bright() + display;
			default -> display;
		};
	}
}