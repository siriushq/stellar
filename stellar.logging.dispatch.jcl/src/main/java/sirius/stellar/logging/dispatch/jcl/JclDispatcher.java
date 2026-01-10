package sirius.stellar.logging.dispatch.jcl;

import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;
import sirius.stellar.logging.LoggerMessage;

import java.time.Instant;

import static java.lang.Thread.currentThread;

/// Implementation of [org.apache.commons.logging.Log] which dispatches to [Logger].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class JclDispatcher implements org.apache.commons.logging.Log {

	private final String name;

	public JclDispatcher(String name) {
		this.name = name;
	}

	//#region is*Enabled
	@Override
	public boolean isDebugEnabled() {
		return Logger.enabled(LoggerLevel.DIAGNOSIS);
	}

	@Override
	public boolean isErrorEnabled() {
		return Logger.enabled(LoggerLevel.ERROR);
	}

	@Override
	public boolean isFatalEnabled() {
		return Logger.enabled(LoggerLevel.ERROR);
	}

	@Override
	public boolean isInfoEnabled() {
		return Logger.enabled(LoggerLevel.INFORMATION);
	}

	@Override
	public boolean isTraceEnabled() {
		return Logger.enabled(LoggerLevel.TRACING);
	}

	@Override
	public boolean isWarnEnabled() {
		return Logger.enabled(LoggerLevel.WARNING);
	}
	//#endregion

	//#region trace*
	@Override
	public void trace(Object message) {
		if (!isTraceEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.TRACING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(String.valueOf(message))
				.dispatch();
	}

	@Override
	public void trace(Object message, Throwable throwable) {
		if (!isTraceEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.TRACING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(String.valueOf(message))
				.throwable(throwable)
				.dispatch();
	}
	//#endregion
	//#region debug*
	@Override
	public void debug(Object message) {
		if (!isDebugEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.DIAGNOSIS)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(String.valueOf(message))
				.dispatch();
	}

	@Override
	public void debug(Object message, Throwable throwable) {
		if (!isDebugEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.DIAGNOSIS)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(String.valueOf(message))
				.throwable(throwable)
				.dispatch();
	}
	//#endregion
	//#region info*
	@Override
	public void info(Object message) {
		if (!isInfoEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(String.valueOf(message))
				.dispatch();
	}

	@Override
	public void info(Object message, Throwable throwable) {
		if (!isInfoEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(String.valueOf(message))
				.throwable(throwable)
				.dispatch();
	}
	//#endregion
	//#region warn*
	@Override
	public void warn(Object message) {
		if (!isWarnEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(String.valueOf(message))
				.dispatch();
	}

	@Override
	public void warn(Object message, Throwable throwable) {
		if (!isWarnEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(String.valueOf(message))
				.throwable(throwable)
				.dispatch();
	}
	//#endregion
	//#region error*
	@Override
	public void error(Object message) {
		if (!isErrorEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.ERROR)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(String.valueOf(message))
				.dispatch();
	}

	@Override
	public void error(Object message, Throwable throwable) {
		if (!isErrorEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.ERROR)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(String.valueOf(message))
				.throwable(throwable)
				.dispatch();
	}
	//#endregion
	//#region fatal*
	@Override
	public void fatal(Object message) {
		if (!isFatalEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.ERROR)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(String.valueOf(message))
				.dispatch();
	}

	@Override
	public void fatal(Object message, Throwable throwable) {
		if (!isFatalEnabled()) return;
		LoggerMessage.builder()
				.level(LoggerLevel.ERROR)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(String.valueOf(message))
				.throwable(throwable)
				.dispatch();
	}
	//#endregion
}