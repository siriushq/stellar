package org.apache.log4j;

import org.apache.log4j.spi.LoggingEvent;
import sirius.stellar.logging.LoggerLevel;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

import static sirius.stellar.facility.Throwables.*;

/**
 * Shadow class for {@code org.apache.log4j.Category}.
 *
 * @author Mechite
 * @since 1.0
 */
public class Category {

	protected static final int STACKTRACE_DEPTH = 2;

	private static final boolean MINIMUM_LEVEL_COVERS_DEBUG = sirius.stellar.logging.Logger.enabled(LoggerLevel.DEBUGGING);
	private static final boolean MINIMUM_LEVEL_COVERS_INFO = sirius.stellar.logging.Logger.enabled(LoggerLevel.INFORMATION);
	private static final boolean MINIMUM_LEVEL_COVERS_WARN = sirius.stellar.logging.Logger.enabled(LoggerLevel.WARNING);
	private static final boolean MINIMUM_LEVEL_COVERS_ERROR = sirius.stellar.logging.Logger.enabled(LoggerLevel.ERROR);

	private final Category parent;
	private final String name;

	private volatile ResourceBundle bundle;

	Category(Category parent, String name) {
		this.parent = parent;
		this.name = name;
	}

	protected Category(String name) {
		this(LogManager.getParentLogger(name), name);
	}

	public void addAppender(Appender newAppender) {
		assert true;
	}

	public void assertLog(boolean assertion, String string) {
		if (!assertion && MINIMUM_LEVEL_COVERS_ERROR) {
			if (!MINIMUM_LEVEL_COVERS_INFO) return;
			sirius.stellar.logging.Logger.dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), this.name, (string));
		}
	}

	public void callAppenders(LoggingEvent event) {
		assert true;
	}

	public void debug(Object object) {
		if (!MINIMUM_LEVEL_COVERS_INFO) return;
		sirius.stellar.logging.Logger.dispatch(Instant.now(), LoggerLevel.DEBUGGING, Thread.currentThread().getName(), this.name, String.valueOf(object));
	}

	public void debug(Object object, Throwable throwable) {
		if (!MINIMUM_LEVEL_COVERS_INFO) return;
		String message = String.valueOf(object);
		if (throwable != null) message += "\n" + stacktrace(throwable);
		sirius.stellar.logging.Logger.dispatch(Instant.now(), LoggerLevel.DEBUGGING, Thread.currentThread().getName(), this.name, message);
	}

	public void error(Object object) {
		if (!MINIMUM_LEVEL_COVERS_INFO) return;
		sirius.stellar.logging.Logger.dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), this.name, String.valueOf(object));
	}

	public void error(Object object, Throwable throwable) {
		if (!MINIMUM_LEVEL_COVERS_INFO) return;
		String message = String.valueOf(object);
		if (throwable != null) message += "\n" + stacktrace(throwable);
		sirius.stellar.logging.Logger.dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), this.name, message);
	}

	public static Logger exists(String name) {
		return LogManager.exists(name);
	}

	public void fatal(Object object) {
		if (!MINIMUM_LEVEL_COVERS_INFO) return;
		sirius.stellar.logging.Logger.dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), this.name, String.valueOf(object));
	}

	public void fatal(Object object, Throwable throwable) {
		if (!MINIMUM_LEVEL_COVERS_INFO) return;
		String message = String.valueOf(object);
		if (throwable != null) message += "\n" + stacktrace(throwable);
		sirius.stellar.logging.Logger.dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), this.name, message);
	}

	protected void forcedLog(String caller, Priority priority, Object object, Throwable throwable) {
		String message = String.valueOf(object);
		if (throwable != null) message += "\n" + stacktrace(throwable);
		sirius.stellar.logging.Logger.dispatch(Instant.now(), convert(priority), Thread.currentThread().getName(), caller, message);
	}

	public boolean getAdditivity() {
		return true;
	}

	public Enumeration getAllAppenders() {
		return Collections.emptyEnumeration();
	}

	public Appender getAppender(String name) {
		return null;
	}

	public Level getEffectiveLevel() {
		return Level.ALL;
	}

	public Priority getChainedPriority() {
		return Level.ALL;
	}

	public static Enumeration getCurrentCategories() {
		return LogManager.getCurrentLoggers();
	}

	public static Category getInstance(String name) {
		return LogManager.getLogger(name);
	}

	public static Category getInstance(Class clazz) {
		return LogManager.getLogger(clazz);
	}

	public final String getName() {
		return name;
	}

	public final Category getParent() {
		return parent;
	}

	public final Level getLevel() {
		return Level.ALL;
	}

	public final Level getPriority() {
		return Level.ALL;
	}

	public static Category getRoot() {
		return LogManager.getRootLogger();
	}

	public ResourceBundle getResourceBundle() {
		return bundle;
	}

	public void info(Object object) {
		if (!MINIMUM_LEVEL_COVERS_INFO) return;
		sirius.stellar.logging.Logger.dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), this.name, String.valueOf(object));
	}

	public void info(Object object, Throwable throwable) {
		if (!MINIMUM_LEVEL_COVERS_INFO) return;
		String message = String.valueOf(object);
		if (throwable != null) message += "\n" + stacktrace(throwable);
		sirius.stellar.logging.Logger.dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), this.name, message);
	}

	public boolean isAttached(Appender appender) {
		return false;
	}

	public boolean isDebugEnabled() {
		return MINIMUM_LEVEL_COVERS_DEBUG && sirius.stellar.logging.Logger.enabled(LoggerLevel.DEBUGGING);
	}

	public boolean isEnabledFor(Priority priority) {
		return sirius.stellar.logging.Logger.enabled(convert(priority));
	}

	public boolean isInfoEnabled() {
		return MINIMUM_LEVEL_COVERS_INFO && sirius.stellar.logging.Logger.enabled(LoggerLevel.INFORMATION);
	}

	public void l7dlog(Priority priority, String key, Throwable throwable) {
		ResourceBundle bundle = this.bundle;
		String message = bundle == null ? key : bundle.getString(key);
		if (throwable != null) message += "\n" + stacktrace(throwable);

		sirius.stellar.logging.Logger.dispatch(Instant.now(), convert(priority), Thread.currentThread().getName(), this.name, message);
	}

	public void l7dlog(Priority priority, String key, Object[] arguments, Throwable throwable) {
		ResourceBundle bundle = this.bundle;
		String message = bundle == null ? key : MessageFormat.format(bundle.getString(key), arguments);
		if (throwable != null) message += "\n" + stacktrace(throwable);

		sirius.stellar.logging.Logger.dispatch(Instant.now(), convert(priority), Thread.currentThread().getName(), this.name, message);
	}

	public void log(Priority priority, Object object, Throwable throwable) {
		String message = String.valueOf(object);
		if (throwable != null) message += "\n" + stacktrace(throwable);
		sirius.stellar.logging.Logger.dispatch(Instant.now(), convert(priority), Thread.currentThread().getName(), this.name, message);
	}

	public void log(Priority priority, Object object) {
		sirius.stellar.logging.Logger.dispatch(Instant.now(), convert(priority), Thread.currentThread().getName(), this.name, String.valueOf(object));
	}

	public void log(String caller, Priority priority, Object object, Throwable throwable) {
		String message = String.valueOf(object);
		if (throwable != null) message += "\n" + stacktrace(throwable);
		sirius.stellar.logging.Logger.dispatch(Instant.now(), convert(priority), Thread.currentThread().getName(), this.name, message);
	}

	public void removeAllAppenders() {
		assert true;
	}

	public void removeAppender(Appender appender) {
		assert true;
	}

	public void removeAppender(String name) {
		assert true;
	}

	public void setAdditivity(boolean additive) {
		assert true;
	}

	public void setLevel(Level level) {
		assert true;
	}

	public void setPriority(Priority priority) {
		assert true;
	}

	public void setResourceBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public static void shutdown() {
		assert true;
	}

	public void warn(Object object) {
		if (!MINIMUM_LEVEL_COVERS_WARN) return;
		sirius.stellar.logging.Logger.dispatch(Instant.now(), convert(Level.WARN), Thread.currentThread().getName(), this.name, String.valueOf(object));
	}

	public void warn(Object object, Throwable throwable) {
		if (!MINIMUM_LEVEL_COVERS_WARN) return;
		String message = String.valueOf(object);
		if (throwable != null) message += "\n" + stacktrace(throwable);
		sirius.stellar.logging.Logger.dispatch(Instant.now(), convert(Level.WARN), Thread.currentThread().getName(), this.name, message);
	}

	/**
	 * Converts the provided level or priority to a {@link LoggerLevel}.
	 */
	private static LoggerLevel convert(Priority priority) {
		return switch (priority.toInt()) {
			case Priority.ALL_INT -> LoggerLevel.ALL;
			case Priority.INFO_INT -> LoggerLevel.INFORMATION;
			case Priority.WARN_INT -> LoggerLevel.WARNING;
			case Priority.ERROR_INT -> LoggerLevel.ERROR;
			case Level.TRACE_INT -> LoggerLevel.STACKTRACE;
			case Priority.DEBUG_INT -> LoggerLevel.DEBUGGING;
			default -> LoggerLevel.OFF;
		};
	}

	/**
	 * Converts the provided level to a {@link Level}.
	 */
	private static Level convert(LoggerLevel level) {
		return switch (level) {
			case ALL -> Level.ALL;

			case INFORMATION -> Level.INFO;
			case WARNING -> Level.WARN;
			case ERROR -> Level.ERROR;

			case STACKTRACE -> Level.TRACE;
			case DEBUGGING, CONFIGURATION -> Level.DEBUG;

			case OFF -> Level.OFF;
		};
	}
}