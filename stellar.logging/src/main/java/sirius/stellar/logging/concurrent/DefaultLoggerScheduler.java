package sirius.stellar.logging.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/// Implementation of [LoggerScheduler] for JVM <21 (single thread executor).
final class DefaultLoggerScheduler
		extends ThreadPoolExecutor
		implements LoggerScheduler {

	/// The initial and maximum number of threads to use.
	private static final int SCHEDULER_THREADS = 1;

	/// The number of milliseconds to keep threads alive without tasks.
	private static final long SCHEDULER_KEEPALIVE = 250L;

	DefaultLoggerScheduler() {
		super(
			SCHEDULER_THREADS, SCHEDULER_THREADS,
			SCHEDULER_KEEPALIVE, MILLISECONDS,
			new LinkedBlockingQueue<>()
		);
	}
}