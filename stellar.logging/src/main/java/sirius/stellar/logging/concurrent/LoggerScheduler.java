package sirius.stellar.logging.concurrent;

import sirius.stellar.annotation.Internal;
import sirius.stellar.logging.Logger;

import java.util.ServiceLoader;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.stream.Collectors.joining;

/// [ScheduledExecutorService] used by [Logger] for scheduling logging.
/// This SPI allows for another implementation to be provided, if desired.
///
/// @implNote By default, a single thread worker is used, and on JVM >21,
/// a single virtual thread executor.
///
/// @since 1.0
public interface LoggerScheduler
	extends ScheduledExecutorService, AutoCloseable {

	/// Obtain a [LoggerScheduler] instance, service-loading the first
	/// alternative implementation found on the class-path/module-path,
	/// if one is available.
	@Internal
	static LoggerScheduler create() {
		try {
			ServiceLoader<LoggerScheduler> loader = ServiceLoader.load(LoggerScheduler.class);
			for (LoggerScheduler scheduler : loader) {
				if (scheduler instanceof DefaultLoggerScheduler) continue;
				return scheduler;
			}
			return new DefaultLoggerScheduler();
		} catch (Throwable throwable) {
			throw new IllegalStateException("Failed wiring alternate logger scheduler", throwable);
		}
	}

	/// Initiates an orderly shutdown where previously submitted tasks are
	/// executed, but no new tasks will be accepted, waiting forever for all
	/// tasks to complete their execution.
	///
	/// @throws IllegalStateException interrupted while waiting
	/// @throws IllegalStateException failed termination
	@Override
	default void close() {
		try {
			boolean terminated = this.isTerminated();
			if (!terminated) this.shutdown();

			terminated = this.awaitTermination(Long.MAX_VALUE, NANOSECONDS);
			if (!terminated) throw new IllegalStateException("Failed termination");
		} catch (InterruptedException exception) {
			String unexecuted = this.shutdownNow()
					.stream()
					.map(Runnable::toString)
					.collect(joining(", ", "[", "]"));
			throw new IllegalStateException("Interrupted " + unexecuted, exception);
		}
	}
}