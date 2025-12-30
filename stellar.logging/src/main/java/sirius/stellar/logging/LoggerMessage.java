package sirius.stellar.logging;

import java.time.Instant;
import java.util.Date;

import static java.text.MessageFormat.*;

/// Represents a message emitted by [Logger]. This is [Comparable],
/// lexicographically comparing the messages by [#time] for sorting.
///
/// @param time The time the message was created.
/// @param level The severity of the message.
/// @param thread The name of the thread.
/// @param name The name of the logger.
/// @param text The text content of the message.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public record LoggerMessage(
	Instant time,
	LoggerLevel level,
	String thread,
	String name,
	String text
) implements Comparable<LoggerMessage> {

	/// Obtain a builder for [LoggerMessage]s.
	public static LoggerMessageBuilder builder() {
		return LoggerMessageBuilder.builder();
	}

	@Override
	public int compareTo(LoggerMessage other) {
		return this.time.compareTo(other.time);
	}

	@Override
	public String toString() {
		return format(
			"LoggerMessage[{0,date,dd/MM/yyyy HH:mm:ss} | {1} | {2} | \"{3}\"]",
			Date.from(this.time),
			this.level,
			this.name,
			this.text
		);
	}
}