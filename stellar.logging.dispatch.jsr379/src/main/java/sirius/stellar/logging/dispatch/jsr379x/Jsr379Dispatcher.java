package sirius.stellar.logging.dispatch.jsr379x;

import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;

import java.io.ObjectStreamException;
import java.io.Serial;
import java.time.Instant;
import java.util.ResourceBundle;

import static java.lang.Thread.*;
import static sirius.stellar.facility.Strings.*;
import static sirius.stellar.facility.Throwables.*;

/// Implementation of [System.Logger] which dispatches to [Logger].
/// There is a lack of handling for [ResourceBundle]s in this implementation.
///
/// @param name The name of the logger.
/// @author Mahied Maruf (mechite)
/// @since 1.0
public record Jsr379Dispatcher(String name) implements System.Logger {

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
		Logger.dispatch(Instant.now(), convert(level), currentThread().getName(), this.name, text);
	}

	@Override
	public void log(Level level, ResourceBundle bundle, String text, Object... arguments) {
		if (!isLoggable(level)) return;
		Logger.dispatch(Instant.now(), convert(level), currentThread().getName(), this.name, format(text, arguments));
	}

	/// Converts the provided level to a [LoggerLevel].
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