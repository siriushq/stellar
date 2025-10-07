package sirius.stellar.logging.dispatch.log4j2x;

import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;

import java.io.Serial;
import java.time.Instant;

import static sirius.stellar.facility.Throwables.*;

/// Implementation of [org.apache.logging.log4j.spi.AbstractLogger] which dispatches to [Logger].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class Log4j2Dispatcher extends org.apache.logging.log4j.spi.AbstractLogger {

	@Serial
	private static final long serialVersionUID = 2981067707921701559L;

	Log4j2Dispatcher(String name) {
		super(name);
	}

	Log4j2Dispatcher(String name, org.apache.logging.log4j.message.MessageFactory factory) {
		super(name, factory);
	}

	/// Converts the provided level to a [LoggerLevel].
	private static LoggerLevel convert(org.apache.logging.log4j.Level level) {
		if (level == org.apache.logging.log4j.Level.ALL) return LoggerLevel.ALL;

		if (level == org.apache.logging.log4j.Level.INFO) return LoggerLevel.INFORMATION;
		if (level == org.apache.logging.log4j.Level.WARN) return LoggerLevel.WARNING;
		if (level == org.apache.logging.log4j.Level.ERROR) return LoggerLevel.ERROR;
		if (level == org.apache.logging.log4j.Level.FATAL) return LoggerLevel.ERROR;

		if (level == org.apache.logging.log4j.Level.TRACE) return LoggerLevel.STACKTRACE;
		if (level == org.apache.logging.log4j.Level.DEBUG) return LoggerLevel.DEBUGGING;

		return LoggerLevel.OFF;
	}

	//#region isEnabled*
	@Override
	public boolean isEnabled(org.apache.logging.log4j.Level level, org.apache.logging.log4j.Marker marker, org.apache.logging.log4j.message.Message message, Throwable t) {
		return Logger.enabled(convert(level));
	}

	@Override
	public boolean isEnabled(org.apache.logging.log4j.Level level, org.apache.logging.log4j.Marker marker, CharSequence message, Throwable t) {
		return Logger.enabled(convert(level));
	}

	@Override
	public boolean isEnabled(org.apache.logging.log4j.Level level, org.apache.logging.log4j.Marker marker, Object message, Throwable t) {
		return Logger.enabled(convert(level));
	}

	@Override
	public boolean isEnabled(org.apache.logging.log4j.Level level, org.apache.logging.log4j.Marker marker, String message, Throwable t) {
		return Logger.enabled(convert(level));
	}

	@Override
	public boolean isEnabled(org.apache.logging.log4j.Level level, org.apache.logging.log4j.Marker marker, String message) {
		return Logger.enabled(convert(level));
	}

	@Override
	public boolean isEnabled(org.apache.logging.log4j.Level level, org.apache.logging.log4j.Marker marker, String message, Object... params) {
		return Logger.enabled(convert(level));
	}

	@Override
	public boolean isEnabled(org.apache.logging.log4j.Level level, org.apache.logging.log4j.Marker marker, String message, Object p0) {
		return Logger.enabled(convert(level));
	}

	@Override
	public boolean isEnabled(org.apache.logging.log4j.Level level, org.apache.logging.log4j.Marker marker, String message, Object p0, Object p1) {
		return Logger.enabled(convert(level));
	}

	@Override
	public boolean isEnabled(org.apache.logging.log4j.Level level, org.apache.logging.log4j.Marker marker, String message, Object p0, Object p1, Object p2) {
		return Logger.enabled(convert(level));
	}

	@Override
	public boolean isEnabled(org.apache.logging.log4j.Level level, org.apache.logging.log4j.Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
		return Logger.enabled(convert(level));
	}

	@Override
	public boolean isEnabled(org.apache.logging.log4j.Level level, org.apache.logging.log4j.Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
		return Logger.enabled(convert(level));
	}

	@Override
	public boolean isEnabled(org.apache.logging.log4j.Level level, org.apache.logging.log4j.Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
		return Logger.enabled(convert(level));
	}

	@Override
	public boolean isEnabled(org.apache.logging.log4j.Level level, org.apache.logging.log4j.Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
		return Logger.enabled(convert(level));
	}

	@Override
	public boolean isEnabled(org.apache.logging.log4j.Level level, org.apache.logging.log4j.Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
		return Logger.enabled(convert(level));
	}

	@Override
	public boolean isEnabled(org.apache.logging.log4j.Level level, org.apache.logging.log4j.Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
		return Logger.enabled(convert(level));
	}

	@Override
	public boolean isEnabled(org.apache.logging.log4j.Level level, org.apache.logging.log4j.Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
		return Logger.enabled(convert(level));
	}
	//#endregion

	@Override
	public void logMessage(String caller, org.apache.logging.log4j.Level level, org.apache.logging.log4j.Marker marker, org.apache.logging.log4j.message.Message message, Throwable throwable) {
		LoggerLevel loggerLevel = convert(level);
		if (!Logger.enabled(loggerLevel)) return;
		String text = message.getFormattedMessage();
		if (marker != null) text = "[" + marker.getName() + "] " + text;
		if (throwable != null) text += "\n" + stacktrace(throwable);
		Logger.dispatch(Instant.now(), loggerLevel, Thread.currentThread().getName(), caller, text);
	}

	@Override
	public org.apache.logging.log4j.Level getLevel() {
		return org.apache.logging.log4j.Level.ALL;
	}
}