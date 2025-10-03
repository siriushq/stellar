package sirius.stellar.logging.dispatch.jsr379x;

import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;

import java.io.ObjectStreamException;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ResourceBundle;

import static sirius.stellar.facility.Strings.*;
import static sirius.stellar.facility.Throwables.*;

/**
 * Implementation of {@link System.Logger} which dispatches to {@link Logger}.
 * There is a lack of handling for {@link ResourceBundle}s in this implementation.
 *
 * @param name The name of the logger.
 * @since 1.0
 * @author Mechite
 */
public record Jsr379Dispatcher(String name) implements System.Logger, Serializable {

	@Serial
	private static final long serialVersionUID = 5261704108702390210L;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isLoggable(Level level) {
		return Logger.enabled(convert(level));
	}

	@Override
	public void log(Level level, ResourceBundle bundle, String text, Throwable throwable) {
		if (!isLoggable(level)) return;
		if (throwable != null) text += "\n" + stacktrace(throwable);
		Logger.dispatch(Instant.now(), convert(level), Thread.currentThread().getName(), this.name, text);
	}

	@Override
	public void log(Level level, ResourceBundle bundle, String text, Object... arguments) {
		if (!isLoggable(level)) return;
		Logger.dispatch(Instant.now(), convert(level), Thread.currentThread().getName(), this.name, format(text, arguments));
	}

	/**
	 * Converts the provided level to a {@link LoggerLevel}.
	 */
	private static LoggerLevel convert(Level level) {
		return switch (level) {
			case ALL -> LoggerLevel.ALL;
			case INFO -> LoggerLevel.INFORMATION;
			case WARNING -> LoggerLevel.WARNING;
			case ERROR -> LoggerLevel.ERROR;
			case TRACE -> LoggerLevel.STACKTRACE;
			case DEBUG -> LoggerLevel.DEBUGGING;
			case OFF -> LoggerLevel.OFF;
		};
	}

	@Serial
	private Object readResolve() throws ObjectStreamException {
		return System.getLogger(this.name);
	}
}