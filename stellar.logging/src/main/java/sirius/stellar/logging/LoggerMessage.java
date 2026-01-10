package sirius.stellar.logging;

import org.jspecify.annotations.Nullable;
import sirius.stellar.logging.fluent.DispatchingBuilder;

import java.time.Instant;
import java.util.Date;

import static java.text.MessageFormat.format;

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

	/// Obtain a builder for [LoggerMessage]s.
	public static Builder builder() {
		return new DispatchingBuilder();
	}

	/// Represents a builder for [LoggerMessage]s, with several utility
	/// methods for appending information, such as stack traces.
	///
	/// A builder can be disabled (due to the logger level being set to a
	/// disabled one), permanently preventing it from being used, and
	/// turning the entire expression into a no-op.
	public interface Builder {
		Builder time(Instant instant);
		Builder thread(String thread);
		Builder name(String name);

		Builder text(String text);
		Builder throwable(@Nullable Throwable throwable);

		/// Modify the level of this builder. This may disable the underlying
		/// builder if the provided level is disabled, releasing all built data
		/// and making it impossible to "re-enable" the builder.
		Builder level(LoggerLevel level);

		/// Build into a [LoggerMessage] instance.
		/// @throws UnsupportedOperationException disabled builder
		LoggerMessage build();

		/// Shorthand for [Logger#dispatch(LoggerMessage)].
		/// This is a no-op for disabled builders.
		void dispatch();
	}
}