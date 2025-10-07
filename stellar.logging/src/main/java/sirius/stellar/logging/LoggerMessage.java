package sirius.stellar.logging;

import sirius.stellar.facility.Orderable;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

import static sirius.stellar.facility.Strings.*;

/**
 * Represents a message emitted by {@link Logger}.
 * <p>
 * Implements {@link Orderable}, lexicographically comparing
 * the messages by {@link LoggerMessage#time} for sorting.
 *
 * @param time The time the message was created.
 * @param level The severity of the message.
 * @param thread The name of the thread.
 * @param name The name of the logger.
 * @param text The text content of the message.
 *
 * @author Mahied Maruf (mechite)
 * @since 1.0
 */
public record LoggerMessage(Instant time, LoggerLevel level, String thread, String name, String text) implements Serializable, Orderable<LoggerMessage> {

	@Serial
	private static final long serialVersionUID = 4451006818948527851L;

	@Override
	public String toString() {
		return format("LoggerMessage[{0,date,dd/MM/yyyy HH:mm:ss} | {1} | {2} | \"{3}\"]", Date.from(this.time), this.level, this.name, this.text);
	}

	@Override
	public void compare(LoggerMessage other, Results results) {
		results.append(this.time, other.time);
	}
}