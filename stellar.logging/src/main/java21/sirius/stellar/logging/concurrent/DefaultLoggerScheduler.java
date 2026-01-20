package sirius.stellar.logging.concurrent;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/// Implementation of [LoggerScheduler] for JVM >21 (virtual thread executor).
final class DefaultLoggerScheduler
		extends ScheduledThreadPoolExecutor
		implements LoggerScheduler {

	DefaultLoggerScheduler() {
		super(0, Thread.ofVirtual().factory());
	}
}