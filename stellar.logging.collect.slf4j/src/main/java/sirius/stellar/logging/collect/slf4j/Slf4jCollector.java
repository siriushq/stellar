package sirius.stellar.logging.collect.slf4j;

import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;
import sirius.stellar.logging.LoggerMessage;
import sirius.stellar.logging.spi.LoggerCollector;
import sirius.stellar.logging.spi.LoggerExtension;

import java.util.HashMap;

/// Implementation of [LoggerCollector] that delegates to SLF4J.
///
/// This is available for users to make use of `stellar.logging` as an API,
/// passing logging messages to their desired underlying implementation in order
/// to retrofit applications making use of more complicated logging setups.
///
/// When using this collector, consider the following behavior:
/// - the level configured for [Logger] is still significant
/// - by default, all messages will be reported as arriving from the thread
///   that the collector is invoked from (managed by the executor in [Logger])
/// - a slight delay may be experienced in timestamps, as the underlying SLF4J
///   implementation will compute the timestamp, and the level mapping.
/// - This is automatically instantiated when on the module-path/class-path,
///   as a [LoggerExtension] service provider.
///
/// Ensure this collector is never on the module path if:
/// - you do not want to use an SLF4J logging backend
/// - you have the SLF4J dispatcher on the classpath, which itself is a logging
///   backend that intercepts SLF4J calls - having both the dispatcher and the
///   collector on the module path will lead to a [StackOverflowError] at best.
///
/// ---------------------------------
/// | [LoggerLevel]   | `org.slf4j` |
/// |-----------------|-------------|
/// | `INFORMATION`   | `INFO`      |
/// | `WARNING`       | `WARN`      |
/// | `ERROR`         | `ERROR`     |
/// | `STACKTRACE`    | `TRACE`     |
/// | `DEBUGGING`     | `DEBUG`     |
/// | `CONFIGURATION` | `DEBUG`     |
/// ---------------------------------
///
/// @since 1.0
public final class Slf4jCollector implements LoggerCollector {

	private final HashMap<String, org.slf4j.Logger> loggers;

	public Slf4jCollector() {
		this.loggers = new HashMap<>();
	}

	@Override
	public void collect(LoggerMessage message) {
		String name = message.name();
		String text = message.text();

		org.slf4j.event.Level level = switch (message.level()) {
			case INFORMATION -> org.slf4j.event.Level.INFO;
			case WARNING -> org.slf4j.event.Level.WARN;
			case ERROR -> org.slf4j.event.Level.ERROR;
			case TRACING -> org.slf4j.event.Level.TRACE;
			case DIAGNOSIS, CONFIGURATION -> org.slf4j.event.Level.DEBUG;
			default -> null;
		};

		org.slf4j.Logger logger = this.loggers.computeIfAbsent(name, org.slf4j.LoggerFactory::getLogger);
		if (level == null) return;

		logger.atLevel(level)
				.setMessage(text)
				.log();
	}
}