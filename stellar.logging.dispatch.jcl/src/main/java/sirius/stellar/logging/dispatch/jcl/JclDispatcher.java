package sirius.stellar.logging.dispatch.jcl;

import sirius.stellar.facility.Throwables;
import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;

import java.io.ObjectStreamException;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/// Implementation of [org.apache.commons.logging.Log] which dispatches to [Logger].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class JclDispatcher implements org.apache.commons.logging.Log, Serializable {

	@Serial
	private static final long serialVersionUID = 7257949654240249339L;

	private final String name;

	public JclDispatcher(String name) {
		this.name = name;
	}

	@Serial
	private Object readResolve() throws ObjectStreamException {
		return JclDispatcherFactory.getInstance().getInstance(this.name);
	}

	//#region is*Enabled
	@Override
	public boolean isDebugEnabled() {
		return Logger.enabled(LoggerLevel.DEBUGGING);
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
		return Logger.enabled(LoggerLevel.STACKTRACE);
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
		Logger.dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), this.name, message.toString());
	}

	@Override
	public void trace(Object message, Throwable throwable) {
		if (!isTraceEnabled()) return;
		Logger.dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), this.name, message.toString() + "\n" + Throwables.stacktrace(throwable));
	}
	//#endregion
	//#region debug*
	@Override
	public void debug(Object message) {
		if (!isDebugEnabled()) return;
		Logger.dispatch(Instant.now(), LoggerLevel.DEBUGGING, Thread.currentThread().getName(), this.name, message.toString());
	}

	@Override
	public void debug(Object message, Throwable throwable) {
		if (!isDebugEnabled()) return;
		Logger.dispatch(Instant.now(), LoggerLevel.DEBUGGING, Thread.currentThread().getName(), this.name, message.toString() + "\n" + Throwables.stacktrace(throwable));
	}
	//#endregion
	//#region info*
	@Override
	public void info(Object message) {
		if (!isInfoEnabled()) return;
		Logger.dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), this.name, message.toString());
	}

	@Override
	public void info(Object message, Throwable throwable) {
		if (!isInfoEnabled()) return;
		Logger.dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), this.name, message.toString() + "\n" + Throwables.stacktrace(throwable));
	}
	//#endregion
	//#region warn*
	@Override
	public void warn(Object message) {
		if (!isWarnEnabled()) return;
		Logger.dispatch(Instant.now(), LoggerLevel.WARNING, Thread.currentThread().getName(), this.name, message.toString());
	}

	@Override
	public void warn(Object message, Throwable throwable) {
		if (!isWarnEnabled()) return;
		Logger.dispatch(Instant.now(), LoggerLevel.WARNING, Thread.currentThread().getName(), this.name, message.toString() + "\n" + Throwables.stacktrace(throwable));
	}
	//#endregion
	//#region error*
	@Override
	public void error(Object message) {
		if (!isErrorEnabled()) return;
		Logger.dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), this.name, message.toString());
	}

	@Override
	public void error(Object message, Throwable throwable) {
		if (!isErrorEnabled()) return;
		Logger.dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), this.name, message.toString() + "\n" + Throwables.stacktrace(throwable));
	}
	//#endregion
	//#region fatal*
	@Override
	public void fatal(Object message) {
		if (!isFatalEnabled()) return;
		Logger.dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), this.name, message.toString());
	}

	@Override
	public void fatal(Object message, Throwable throwable) {
		if (!isFatalEnabled()) return;
		Logger.dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), this.name, message.toString() + "\n" + Throwables.stacktrace(throwable));
	}
	//#endregion
}