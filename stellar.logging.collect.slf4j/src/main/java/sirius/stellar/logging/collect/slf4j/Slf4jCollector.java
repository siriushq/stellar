package sirius.stellar.logging.collect.slf4j;

import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;
import sirius.stellar.logging.LoggerMessage;
import sirius.stellar.logging.collect.Collector;

import java.util.HashMap;

/// Implementation of [Collector] that delegates to SLF4J.
///
/// This is available for users to make use of `stellar.logging` as an API,
/// passing logging messages to their desired underlying implementation in order
/// to retrofit applications making use of more complicated logging setups.
///
/// When using this logger, consider the following behavior:
/// - the level configured for [Logger] is still significant
/// - by default, all messages will be reported as arriving from the thread
///   that the collector is invoked from (managed by the executor in [Logger])
/// - a slight delay may be experienced in timestamps, as the underlying SLF4J
///   implementation will compute the timestamp, and the level mapping.
/// - [Slf4jCollectorProvider] will create an instance of this collector
///   automatically from the static initialization in [Logger], provided this
///   collector is on the module path.
///
/// Ensure this collector is NOT on the module path if:
/// - you do not want to use an SLF4J logging backend
/// - you have the SLF4J dispatcher on the classpath, which itself is a logging
/// backend which intercepts SLF4J calls - having both the dispatcher and the
/// collector on the module path will lead to a [StackOverflowError].
///
/// ---------------------------------
/// | [LoggerLevel]   | `org.slf4j` |
/// |-----------------|-------------|
/// | `ALL`           | `INFO`      |
/// | `INFORMATION`   | `INFO`      |
/// | `WARNING`       | `WARN`      |
/// | `ERROR`         | `ERROR`     |
/// | `STACKTRACE`    | `TRACE`     |
/// | `DEBUGGING`     | `DEBUG`     |
/// | `CONFIGURATION` | `DEBUG`     |
/// | `OFF`           | `DEBUG`     |
/// ---------------------------------
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public record Slf4jCollector(HashMap<String, org.slf4j.Logger> loggers) implements Collector {

	public Slf4jCollector() {
		this(new HashMap<>());
	}

	@Override
	public void collect(LoggerMessage message) {
		this.loggers.computeIfAbsent(message.name(), org.slf4j.LoggerFactory::getLogger)
				.atLevel(switch (message.level()) {
					case ALL, INFORMATION -> org.slf4j.event.Level.INFO;
					case WARNING -> org.slf4j.event.Level.WARN;
					case ERROR -> org.slf4j.event.Level.ERROR;
					case TRACING -> org.slf4j.event.Level.TRACE;
					case DIAGNOSIS, CONFIGURATION, OFF -> org.slf4j.event.Level.DEBUG;
				})
				.setMessage(message.text())
				.log();
	}
}