package sirius.stellar.logging.collect.slf4j;

import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;
import sirius.stellar.logging.LoggerMessage;
import sirius.stellar.logging.collect.Collector;

import java.io.Serial;
import java.util.HashMap;

/// Implementation of [Collector] that delegates to SLF4J.
///
/// This is not used by default and is available for users to make use of
/// `stellar.logging.*` as an API, passing logging messages to their
/// desired underlying implementation in order to retrofit applications
/// making use of more complicated logging setups.
///
/// Notable factors when using this collector include the fact that the level
/// configured for [Logger] is still significant, the fact that all messages
/// will be reported as arriving from the thread that the collector is invoked
/// from (which is managed by the executor in [Logger]), the slight delay that
/// may be experienced in timestamps as the underlying SLF4J implementation
/// will compute the timestamp, and the level mapping.
///
/// Do not use this implementation if you do not have a different implementation
/// for SLF4J available on the classpath or module path, as it will lead to an
/// endless loop of logging being dispatched to this collector, from this
/// collector to the SLF4J dispatcher, and then back to this collector.
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

	@Serial
	private static final long serialVersionUID = 4175820898924806606L;

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
					case STACKTRACE -> org.slf4j.event.Level.TRACE;
					case DEBUGGING, CONFIGURATION, OFF -> org.slf4j.event.Level.DEBUG;
				})
				.setMessage(message.text())
				.log();
	}
}