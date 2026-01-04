package org.apache.log4j;

import org.apache.log4j.spi.LoggerFactory;
import sirius.stellar.logging.LoggerLevel;

import java.time.Instant;

import static java.lang.Thread.currentThread;

/// Shadow class for `org.apache.log4j.Logger`.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public class Logger extends Category {

	private static final boolean MINIMUM_LEVEL_COVERS_TRACE = sirius.stellar.logging.Logger.enabled(LoggerLevel.STACKTRACE);

	Logger(Logger parent, String name) {
		super(parent, name);
	}

	protected Logger(String name) {
		super(LogManager.getParentLogger(name), name);
	}

	public static Logger getLogger(String name) {
		return LogManager.getLogger(name);
	}

	public static Logger getLogger(Class clazz) {
		return LogManager.getLogger(clazz.getName());
	}

	public static Logger getRootLogger() {
		return LogManager.getRootLogger();
	}

	public static Logger getLogger(final String name, final LoggerFactory factory) {
		return LogManager.getLogger(name, factory);
	}

	public void trace(Object object) {
		if (!MINIMUM_LEVEL_COVERS_TRACE) return;
		sirius.stellar.logging.Logger.dispatch(Instant.now(), LoggerLevel.STACKTRACE, currentThread().getName(), this.getName(), String.valueOf(object));
	}

	public void trace(Object object, Throwable throwable) {
		if (!MINIMUM_LEVEL_COVERS_TRACE) return;
		String message = String.valueOf(object);
		if (throwable != null) message += "\n" + stacktrace(throwable);
		sirius.stellar.logging.Logger.dispatch(Instant.now(), LoggerLevel.STACKTRACE, currentThread().getName(), this.getName(), message);
	}

	public boolean isTraceEnabled() {
		return MINIMUM_LEVEL_COVERS_TRACE && sirius.stellar.logging.Logger.enabled(LoggerLevel.STACKTRACE);
	}
}