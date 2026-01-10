package sirius.stellar.logging.dispatch.log4j;

import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;
import sirius.stellar.logging.LoggerMessage;
import sirius.stellar.logging.dispatch.Dispatcher;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

/// Implementation of [org.apache.log4j.AppenderSkeleton] that delegates to [Logger].
///
/// -------------------------------
/// | `o.a.log4j` | [LoggerLevel] |
/// |-------------|---------------|
/// | `ALL`       | `ALL`         |
/// | `INFO`      | `INFORMATION` |
/// | `WARN`      | `WARNING`     |
/// | `ERROR`     | `ERROR`       |
/// | `TRACE`     | `TRACING`     |
/// | `DEBUG`     | `DIAGNOSIS`   |
/// | `OFF`       | `ERROR`       |
/// -------------------------------
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class Log4jDispatcher
		extends org.apache.log4j.AppenderSkeleton
		implements Dispatcher {

	private final AtomicBoolean closed;

	Log4jDispatcher() {
		this.closed = new AtomicBoolean(false);
	}

	@Override
	public void wire() {
		org.apache.log4j.Logger.getRootLogger().addAppender(this);
	}

	@Override
	protected void append(org.apache.log4j.spi.LoggingEvent event) {
		if (this.closed.get()) return;
		Throwable throwable = (event.getThrowableInformation() != null) ?
				event.getThrowableInformation().getThrowable() :
				null;
		LoggerMessage.builder()
				.level(convert(event.getLevel()))
				.time(Instant.ofEpochMilli(event.getTimeStamp()))
				.thread(event.getThreadName())
				.name(event.getLoggerName())
				.text(event.getRenderedMessage())
				.throwable(throwable)
				.dispatch();
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	/// Converts the provided level or priority to a [LoggerLevel].
	private static LoggerLevel convert(org.apache.log4j.Priority priority) {
		return switch (priority.toInt()) {
			case org.apache.log4j.Priority.ALL_INT -> LoggerLevel.ALL;
			case org.apache.log4j.Priority.INFO_INT -> LoggerLevel.INFORMATION;
			case org.apache.log4j.Priority.WARN_INT -> LoggerLevel.WARNING;
			case org.apache.log4j.Priority.ERROR_INT -> LoggerLevel.ERROR;
			case org.apache.log4j.Level.TRACE_INT -> LoggerLevel.TRACING;
			case org.apache.log4j.Priority.DEBUG_INT -> LoggerLevel.DIAGNOSIS;
			default -> LoggerLevel.OFF;
		};
	}

	@Override
	public void close() {
		this.closed.set(true);
	}
}