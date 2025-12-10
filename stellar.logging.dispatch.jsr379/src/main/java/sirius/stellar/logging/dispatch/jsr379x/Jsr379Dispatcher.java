package sirius.stellar.logging.dispatch.jsr379x;

import org.jspecify.annotations.Nullable;
import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;

import java.time.Instant;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import static java.lang.Thread.*;
import static sirius.stellar.facility.Strings.*;
import static sirius.stellar.facility.Throwables.*;
import static sirius.stellar.logging.LoggerFormat.*;

/// Implementation of [System.Logger] which dispatches to [Logger].
/// There is a lack of handling for [ResourceBundle]s in this implementation.
///
/// @param name The name of the logger.
/// @author Mahied Maruf (mechite)
/// @since 1.0
public record Jsr379Dispatcher(String name, @Nullable ResourceBundle bundle) implements System.Logger {

	public Jsr379Dispatcher(String name) {
		this(name, null);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isLoggable(Level level) {
		return Logger.enabled(convert(level));
	}

	@Override
	public void log(Level level, String text) {
		if (!isLoggable(level)) return;
		log(level, this.bundle, text);
	}

	@Override
	public void log(Level level, Supplier<String> textSupplier) {
		if (!isLoggable(level)) return;
		log(level, this.bundle, textSupplier.get());
	}

	@Override
	public void log(Level level, Object object) {
		if (!isLoggable(level)) return;
		this.log(level, this.bundle, object.toString());
	}

	@Override
	public void log(Level level, String text, Throwable throwable) {
		if (!isLoggable(level)) return;
		this.log(level, this.bundle, text, throwable);
	}

	@Override
	public void log(Level level, Supplier<String> textSupplier, Throwable throwable) {
		if (!isLoggable(level)) return;
		log(level, this.bundle, textSupplier.get(), throwable);
	}

	@Override
	public void log(Level level, String text, Object... arguments) {
		if (!isLoggable(level)) return;
		this.log(level, this.bundle, text, arguments);
	}

	@Override
	public void log(Level level, @Nullable ResourceBundle bundle, String text, @Nullable Throwable throwable) {
		if (!isLoggable(level)) return;
		if (bundle != null && bundle.containsKey(text)) text = bundle.getString(text);
		if (throwable != null) text += "\n" + stacktrace(throwable);

		Logger.dispatch(Instant.now(), convert(level), currentThread().getName(), this.name, text);
	}

	@Override
	public void log(Level level, @Nullable ResourceBundle bundle, String text, Object... arguments) {
		if (!isLoggable(level)) return;
		if (bundle != null && bundle.containsKey(text)) text = bundle.getString(text);

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
}