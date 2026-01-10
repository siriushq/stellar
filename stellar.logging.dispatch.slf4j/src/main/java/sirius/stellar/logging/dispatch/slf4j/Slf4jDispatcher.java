package sirius.stellar.logging.dispatch.slf4j;

import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;
import sirius.stellar.logging.LoggerMessage;

import java.time.Instant;

import static java.lang.Thread.currentThread;

/// Implementation of [org.slf4j.Logger] which dispatches to [Logger].
///
/// There is suboptimal handling of [org.slf4j.Marker]s in this implementation;
/// all the marker-related methods simply call [org.slf4j.Marker#getName()] and
/// prefix the message with `MARKER_NAME: This is an example message`.
///
/// @param name The name of the logger.
/// @author Mahied Maruf (mechite)
/// @since 1.0
public record Slf4jDispatcher(String name) implements org.slf4j.Logger {

	@Override
	public String getName() {
		return this.name;
	}

	//#region trace*
	@Override
	public boolean isTraceEnabled() {
		return Logger.enabled(LoggerLevel.TRACING);
	}

	@Override
	public void trace(String text) {
		if (!isTraceEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.TRACING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void trace(String text, Object argument) {
		if (!isTraceEnabled()) return;
		text = org.slf4j.helpers.MessageFormatter.format(text, argument).getMessage();
		LoggerMessage.builder()
				.level(LoggerLevel.TRACING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void trace(String text, Object argument1, Object argument2) {
		if (!isTraceEnabled()) return;
		text = org.slf4j.helpers.MessageFormatter.format(text, argument1, argument2).getMessage();
		LoggerMessage.builder()
				.level(LoggerLevel.TRACING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void trace(String text, Object... arguments) {
		if (!isTraceEnabled()) return;
		text = org.slf4j.helpers.MessageFormatter.format(text, arguments).getMessage();
		LoggerMessage.builder()
				.level(LoggerLevel.TRACING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void trace(String text, Throwable throwable) {
		if (!isTraceEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.TRACING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.throwable(throwable)
				.dispatch();
	}
	//#endregion
	//#region trace* [Marker]
	@Override
	public boolean isTraceEnabled(org.slf4j.Marker marker) {
		return this.isTraceEnabled();
	}

	@Override
	public void trace(org.slf4j.Marker marker, String text) {
		if (!this.isTraceEnabled()) return;
		this.trace(marker.getName() + ": " + text);
	}

	@Override
	public void trace(org.slf4j.Marker marker, String text, Object argument) {
		if (!this.isTraceEnabled()) return;
		this.trace(marker.getName() + ": " + text, argument);
	}

	@Override
	public void trace(org.slf4j.Marker marker, String text, Object argument1, Object argument2) {
		if (!this.isTraceEnabled()) return;
		this.trace(marker.getName() + ": " + text, argument1, argument2);
	}

	@Override
	public void trace(org.slf4j.Marker marker, String text, Object... arguments) {
		if (!this.isTraceEnabled()) return;
		this.trace(marker.getName() + ": " + text, arguments);
	}

	@Override
	public void trace(org.slf4j.Marker marker, String text, Throwable throwable) {
		if (!this.isTraceEnabled()) return;
		this.trace(marker.getName() + ": " + text, throwable);
	}
	//#endregion

	//#region debug*
	@Override
	public boolean isDebugEnabled() {
		return Logger.enabled(LoggerLevel.DIAGNOSIS);
	}

	@Override
	public void debug(String text) {
		if (!isDebugEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.DIAGNOSIS)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void debug(String text, Object argument) {
		if (!isDebugEnabled()) return;
		text = org.slf4j.helpers.MessageFormatter.format(text, argument).getMessage();
		LoggerMessage.builder()
				.level(LoggerLevel.DIAGNOSIS)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void debug(String text, Object argument1, Object argument2) {
		if (!isDebugEnabled()) return;
		text = org.slf4j.helpers.MessageFormatter.format(text, argument1, argument2).getMessage();
		LoggerMessage.builder()
				.level(LoggerLevel.DIAGNOSIS)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void debug(String text, Object... arguments) {
		if (!isDebugEnabled()) return;
		text = org.slf4j.helpers.MessageFormatter.format(text, arguments).getMessage();
		LoggerMessage.builder()
				.level(LoggerLevel.DIAGNOSIS)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void debug(String text, Throwable throwable) {
		if (!isDebugEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.DIAGNOSIS)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.throwable(throwable)
				.dispatch();
	}
	//#endregion
	//#region debug* [Marker]
	@Override
	public boolean isDebugEnabled(org.slf4j.Marker marker) {
		return this.isDebugEnabled();
	}

	@Override
	public void debug(org.slf4j.Marker marker, String text) {
		if (!this.isDebugEnabled()) return;
		this.debug(marker.getName() + ": " + text);
	}

	@Override
	public void debug(org.slf4j.Marker marker, String text, Object argument) {
		if (!this.isDebugEnabled()) return;
		this.debug(marker.getName() + ": " + text, argument);
	}

	@Override
	public void debug(org.slf4j.Marker marker, String text, Object argument1, Object argument2) {
		if (!this.isDebugEnabled()) return;
		this.debug(marker.getName() + ": " + text, argument1, argument2);
	}

	@Override
	public void debug(org.slf4j.Marker marker, String text, Object... arguments) {
		if (!this.isDebugEnabled()) return;
		this.debug(marker.getName() + ": " + text, arguments);
	}

	@Override
	public void debug(org.slf4j.Marker marker, String text, Throwable throwable) {
		if (!this.isDebugEnabled()) return;
		this.debug(marker.getName() + ": " + text, throwable);
	}
	//#endregion

	//#region info*
	@Override
	public boolean isInfoEnabled() {
		return Logger.enabled(LoggerLevel.INFORMATION);
	}

	@Override
	public void info(String text) {
		if (!isInfoEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void info(String text, Object argument) {
		if (!isInfoEnabled()) return;
		text = org.slf4j.helpers.MessageFormatter.format(text, argument).getMessage();
		LoggerMessage.builder()
				.level(LoggerLevel.INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void info(String text, Object argument1, Object argument2) {
		if (!isInfoEnabled()) return;
		text = org.slf4j.helpers.MessageFormatter.format(text, argument1, argument2).getMessage();
		LoggerMessage.builder()
				.level(LoggerLevel.INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void info(String text, Object... arguments) {
		if (!isInfoEnabled()) return;
		text = org.slf4j.helpers.MessageFormatter.format(text, arguments).getMessage();
		LoggerMessage.builder()
				.level(LoggerLevel.INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void info(String text, Throwable throwable) {
		if (!isInfoEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.throwable(throwable)
				.dispatch();
	}
	//#endregion
	//#region info* [Marker]
	@Override
	public boolean isInfoEnabled(org.slf4j.Marker marker) {
		return this.isInfoEnabled();
	}

	@Override
	public void info(org.slf4j.Marker marker, String text) {
		if (!this.isInfoEnabled()) return;
		this.info(marker.getName() + ": " + text);
	}

	@Override
	public void info(org.slf4j.Marker marker, String text, Object argument) {
		if (!this.isInfoEnabled()) return;
		this.info(marker.getName() + ": " + text, argument);
	}

	@Override
	public void info(org.slf4j.Marker marker, String text, Object argument1, Object argument2) {
		if (!this.isInfoEnabled()) return;
		this.info(marker.getName() + ": " + text, argument1, argument2);
	}

	@Override
	public void info(org.slf4j.Marker marker, String text, Object... arguments) {
		if (!this.isInfoEnabled()) return;
		this.info(marker.getName() + ": " + text, arguments);
	}

	@Override
	public void info(org.slf4j.Marker marker, String text, Throwable throwable) {
		if (!this.isInfoEnabled()) return;
		this.info(marker.getName() + ": " + text, throwable);
	}
	//#endregion

	//#region warn*
	@Override
	public boolean isWarnEnabled() {
		return Logger.enabled(LoggerLevel.WARNING);
	}

	@Override
	public void warn(String text) {
		if (!isWarnEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void warn(String text, Object argument) {
		if (!isWarnEnabled()) return;
		text = org.slf4j.helpers.MessageFormatter.format(text, argument).getMessage();
		LoggerMessage.builder()
				.level(LoggerLevel.WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void warn(String text, Object argument1, Object argument2) {
		if (!isWarnEnabled()) return;
		text = org.slf4j.helpers.MessageFormatter.format(text, argument1, argument2).getMessage();
		LoggerMessage.builder()
				.level(LoggerLevel.WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void warn(String text, Object... arguments) {
		if (!isWarnEnabled()) return;
		text = org.slf4j.helpers.MessageFormatter.format(text, arguments).getMessage();
		LoggerMessage.builder()
				.level(LoggerLevel.WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void warn(String text, Throwable throwable) {
		if (!isWarnEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.throwable(throwable)
				.dispatch();
	}
	//#endregion
	//#region warn* [Marker]
	@Override
	public boolean isWarnEnabled(org.slf4j.Marker marker) {
		return this.isWarnEnabled();
	}

	@Override
	public void warn(org.slf4j.Marker marker, String text) {
		if (!this.isWarnEnabled()) return;
		this.warn(marker.getName() + ": " + text);
	}

	@Override
	public void warn(org.slf4j.Marker marker, String text, Object argument) {
		if (!this.isWarnEnabled()) return;
		this.warn(marker.getName() + ": " + text, argument);
	}

	@Override
	public void warn(org.slf4j.Marker marker, String text, Object argument1, Object argument2) {
		if (!this.isWarnEnabled()) return;
		this.warn(marker.getName() + ": " + text, argument1, argument2);
	}

	@Override
	public void warn(org.slf4j.Marker marker, String text, Object... arguments) {
		if (!this.isWarnEnabled()) return;
		this.warn(marker.getName() + ": " + text, arguments);
	}

	@Override
	public void warn(org.slf4j.Marker marker, String text, Throwable throwable) {
		if (!this.isWarnEnabled()) return;
		this.warn(marker.getName() + ": " + text, throwable);
	}
	//#endregion

	//#region error*
	@Override
	public boolean isErrorEnabled() {
		return Logger.enabled(LoggerLevel.ERROR);
	}

	@Override
	public void error(String text) {
		if (!isErrorEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.ERROR)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void error(String text, Object argument) {
		if (!isErrorEnabled()) return;
		text = org.slf4j.helpers.MessageFormatter.format(text, argument).getMessage();
		LoggerMessage.builder()
				.level(LoggerLevel.ERROR)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void error(String text, Object argument1, Object argument2) {
		if (!isErrorEnabled()) return;
		text = org.slf4j.helpers.MessageFormatter.format(text, argument1, argument2).getMessage();
		LoggerMessage.builder()
				.level(LoggerLevel.ERROR)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void error(String text, Object... arguments) {
		if (!isErrorEnabled()) return;
		text = org.slf4j.helpers.MessageFormatter.format(text, arguments).getMessage();
		LoggerMessage.builder()
				.level(LoggerLevel.ERROR)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
	}

	@Override
	public void error(String text, Throwable throwable) {
		if (!isErrorEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.ERROR)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.throwable(throwable)
				.dispatch();
	}
	//#endregion
	//#region error* [Marker]
	@Override
	public boolean isErrorEnabled(org.slf4j.Marker marker) {
		return this.isErrorEnabled();
	}

	@Override
	public void error(org.slf4j.Marker marker, String text) {
		if (!this.isErrorEnabled()) return;
		this.error(marker.getName() + ": " + text);
	}

	@Override
	public void error(org.slf4j.Marker marker, String text, Object argument) {
		if (!this.isErrorEnabled()) return;
		this.error(marker.getName() + ": " + text, argument);
	}

	@Override
	public void error(org.slf4j.Marker marker, String text, Object argument1, Object argument2) {
		if (!this.isErrorEnabled()) return;
		this.error(marker.getName() + ": " + text, argument1, argument2);
	}

	@Override
	public void error(org.slf4j.Marker marker, String text, Object... arguments) {
		if (!this.isErrorEnabled()) return;
		this.error(marker.getName() + ": " + text, arguments);
	}

	@Override
	public void error(org.slf4j.Marker marker, String text, Throwable throwable) {
		if (!this.isErrorEnabled()) return;
		this.error(marker.getName() + ": " + text, throwable);
	}
	//#endregion
}