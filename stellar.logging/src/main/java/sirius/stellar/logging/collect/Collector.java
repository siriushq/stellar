package sirius.stellar.logging.collect;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;
import sirius.stellar.facility.annotation.Internal;
import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerMessage;

import java.io.PrintStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/// Represents an operation that takes place when a logger message is emitted.
///
/// Static methods are also available under this interface for obtaining default
/// implementations of the interface, such as for console logging.
///
/// When implementing this interface, the [LoggerMessage] object emitted when
/// collecting a message can have heavy assertions made against it and any heavy
/// operations needed to, e.g., publish messages through a distributed log, can be
/// done safely, as it is expected that collectors are always invoked asynchronously.
///
/// This means that performance is predictable, log ordering is never affected, and
/// the impact logging has on your application is negligible. However, it is still
/// suggested that [Collector#task(Callable)] is used to schedule a task if it is
/// heavy enough that mixing the task with other virtual threads is desirable, i.e.,
/// if it does any heavy I/O operations that could affect the general performance
/// of your application.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
@FunctionalInterface
public interface Collector extends AutoCloseable, Serializable {

	/// An executor used for scheduling I/O tasks that are performed inside collectors.
	///
	/// Given a different executor is desired, the [#task(Callable)] method can be overridden,
	/// or simply not used in favor of submitting tasks a different way.
	///
	/// However, if a different, non-virtual executor is desired, it is likely scheduling a
	/// task with the semantics of that method is not the desired behaviour.
	///
	/// The [#collect(LoggerMessage)] method is already called on a dedicated logging
	/// thread by default, but for extremely heavy I/O purposes, whenever this separation
	/// is actually interfering with application performance/throughput, the semantics of
	/// that method is more desirable, hence the presence of this executor.
	@Internal
	ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

	/// Runs when a logger message is emitted.
	///
	/// This should only be used for lightweight logging I/O, and anything that
	/// may affect the performance of the application should instead be scheduled
	/// with [#task(Callable)].
	///
	/// @since 1.0
	void collect(LoggerMessage message);

	/// Runs when this collector is closed.
	///
	/// This method is implemented by default to allow for simple collectors to
	/// be defined by implementing only one abstract method, i.e., with a lambda,
	/// if they do not maintain any closeable resources.
	///
	/// @since 1.0
	@Override
	default void close() throws Exception {
		assert true;
	}

	/// Registers a daemon task to be run.
	///
	/// This method should never be overridden unless a different method of running the task is
	/// desired. By default, a [Executors#newVirtualThreadPerTaskExecutor()] is used as it
	/// allows for the I/O tasks that are expected to happen in these tasks to not interfere
	/// with the throughput of the application.
	///
	/// @return A future that never returns a value, which can be canceled in order to
	/// interrupt the task, usually useful during [#close()]. This should be done with the
	/// `mayInterruptIfRunning` flag set to `true`, i.e., `task.cancel(true)`.
	///
	/// This is not done by default in that method, so if any tasks are started with this method,
	/// it is suggested that the [#close()] method is overridden to properly clean up the task,
	/// interrupting it. However, if that method is not overridden, then expected it is that the
	/// task will end, and [Logger#close] will block until it does if it is invoked.
	///
	/// @since 1.0
	default Future<@Nullable Void> task(Callable<@Nullable Void> callable) {
		return executor.submit(callable);
	}

	/// Returns an instance that prints to console (`stderr`).
	///
	/// This method can only be called once across the application lifecycle as it
	/// runs [System#setOut(PrintStream)] and [System#setErr(PrintStream)] to create
	/// a dispatcher for later `stdout`/`stderr` calls to redirect to [Logger].
	///
	/// @see #consoleOut()
	/// @since 1.0
	static Collector console() {
		return ConsoleCollector.overriding(System.err);
	}

	/// Returns an instance that prints to console (`stdout`).
	/// This method has the same semantics as [#console()].
	///
	/// @since 1.0
	static Collector consoleOut() {
		return ConsoleCollector.overriding(System.out);
	}

	/// Returns an instance that prints to the provided [PrintStream], with
	/// the same output as [#console()]/[#consoleOut()], but not overriding the
	/// global `stdout`/`stderr` streams as dispatchers.
	///
	/// @since 1.0
	static Collector consoleStream(PrintStream stream) {
		return new ConsoleCollector(System.out);
	}

	/// Returns an instance that prints CSV formatted output to log files.
	///
	/// Output is written to `./logging/` relative to working directory
	/// and rolls to a new file every 12 hours.
	///
	/// @see #file(Path)
	/// @see #file(Path, Duration)
	/// @since 1.0
	static Collector file() {
		return file(Path.of("logging"));
	}

	/// Returns an instance that prints CSV formatted output to log files.
	/// It rolls to a new file every 12 hours.
	///
	/// @param path directory to write log files to
	///
	/// @see #file()
	/// @see #file(Path, Duration)
	/// @since 1.0
	@Contract("_ -> new")
	static Collector file(Path path) {
		return file(path, Duration.ofHours(12));
	}

	/// Returns an instance that prints CSV formatted output to log files.
	///
	/// @param path directory to write log files to
	/// @param duration how often to roll to a new file
	///
	/// @see #file()
	/// @see #file(Path)
	/// @since 1.0
	@Contract("_, _ -> new")
	static Collector file(Path path, Duration duration) {
		return new FileCollector(path, duration);
	}

	/// Provides an instance of a [Collector].
	///
	/// This provides automatic registration of collectors, but is not required if
	/// the collector should be optional (and registered manually using the static
	/// [Logger#collector(Collector)] method.
	///
	/// @author Mahied Maruf (mechite)
	/// @since 1.0
	interface Provider {
		Collector create();
	}
}