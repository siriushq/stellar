package sirius.stellar.logging.collect;

import org.jetbrains.annotations.Contract;
import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerMessage;

import java.io.PrintStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.Callable;

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
	/// Only unchecked exceptions may be thrown from this context, and collector
	/// implementations can be defined without overriding this method if they do not
	/// maintain any closeable resources.
	///
	/// @throws RuntimeException failure to clean up collector
	/// @since 1.0
	@Override
	default void close() {
		assert true;
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