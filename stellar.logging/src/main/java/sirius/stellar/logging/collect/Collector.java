package sirius.stellar.logging.collect;

import org.jetbrains.annotations.Contract;
import sirius.stellar.facility.doctation.Internal;
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

/**
 * Represents an operation that takes place when a logger message is emitted.
 * <p>
 * Static methods are also available under this interface for obtaining default
 * implementations of the interface, such as for console logging.
 * <p>
 * When implementing this interface, the {@link LoggerMessage} object emitted when
 * collecting a message can have heavy assertions made against it and any heavy
 * operations needed to, e.g., publish messages through a distributed log, can be
 * done safely, as it is expected that collectors are always invoked asynchronously.
 * <p>
 * This means that performance is predictable, log ordering is never affected, and
 * the impact logging has on your application is negligible. However, it is still
 * suggested that {@link Collector#task(Callable)} is used to schedule a task if it
 * is heavy enough that mixing the task with other virtual threads is desirable, i.e.,
 * if it does any heavy I/O operations that could affect general performance of your
 * application.
 *
 * @since 1.0
 * @author Mechite
 */
@FunctionalInterface
public interface Collector extends AutoCloseable, Serializable {

	/**
	 * An executor used for scheduling I/O tasks that are performed inside collectors.
	 * <p>
	 * Given a different executor is desired, the {@link Collector#task(Callable)} method
	 * can be overridden, or simply not used in favor of submitting tasks a different way.
	 * <p>
	 * However, if a different, non-virtual executor is desired, it is likely scheduling a
	 * task with the semantics of that method is not the desired behaviour.
	 * <p>
	 * The {@link #collect(LoggerMessage)} method is already called on a dedicated logging
	 * thread by default, but for extremely heavy I/O purposes, whenever this separation
	 * is actually interfering with application performance/throughput, the semantics of
	 * that method is more desirable, hence the presence of this executor.
	 */
	@Internal
	ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

	/**
	 * Runs when a logger message is emitted.
	 * This should only be used for lightweight logging I/O, and anything that
	 * may affect the performance of the application should instead be scheduled
	 * with {@link Collector#task(Callable)}
	 *
	 * @since 1.0
	 */
	void collect(LoggerMessage message);

	/**
	 * Runs when this collector is closed.
	 * <p>
	 * This method is implemented by default to allow for simple collectors to
	 * be defined by implementing only one abstract method, i.e., with a lambda,
	 * if they do not maintain any closeable resources.
	 *
	 * @since 1.0
	 */
	@Override
	default void close() throws Exception {
		assert true;
	}

	/**
	 * Registers a daemon task to be run.
	 * <p>
	 * This method should never be overridden unless a different method of running the task is
	 * desired. By default, a {@link Executors#newVirtualThreadPerTaskExecutor()} is used as it
	 * allows for the I/O tasks that are expected to happen in these tasks to not interfere with
	 * the throughput of the application.
	 *
	 * @return A future that never returns a value, which can be canceled in order to interrupt
	 * the task, usually useful during {@link Collector#close()}. This should be done with the
	 * {@code mayInterruptIfRunning} flag set to {@code true}, i.e., {@code task.cancel(true)}.
	 * <p>
	 * This is not done by default in that method, so if any tasks are started with this method,
	 * it is suggested that the {@link Collector#close()} method is overridden to properly clean
	 * up the task, interrupting it. However, if that method is not overridden, then expected it
	 * is that the task will end, and {@link Logger#close} will block until it does if it is
	 * invoked.
 	 *
	 * @since 1.0
	 */
	default Future<Void> task(Callable<Void> callable) {
		return executor.submit(callable);
	}

	/**
	 * Returns an instance that prints to console.
	 * <p>
	 * This method can only be called once across the application lifecycle as it
	 * runs {@link System#setOut(PrintStream)} and {@link System#setErr(PrintStream)}.
	 * <p>
	 * This essentially means that this not only provides a collector that collects
	 * logs to {@code stdout}, but also it sets a dispatcher so that any later calls
	 * to try and output to {@code stdout} will be redirected to logging.
	 *
	 * @since 1.0
	 */
	static Collector console() {
		return ConsoleCollector.get();
	}

	/**
	 * Returns an instance that prints to log files.
	 * This method does not accept a path argument and defaults to {@code logging/}.
	 * It rolls to a new file every 12 hours.
	 *
	 * @see Collector#file(Path)
	 * @see Collector#file(Path, Duration)
	 * @since 1.0
	 */
	static Collector file() {
		return file(Path.of("logging"));
	}

	/**
	 * Returns an instance that prints to log files.
	 * It rolls to a new file every 12 hours.
	 *
	 * @param path The root of where the files are output.
	 *
	 * @see Collector#file()
	 * @see Collector#file(Path, Duration)
	 * @since 1.0
	 */
	@Contract("null -> fail; !null -> new")
	static Collector file(Path path) {
		return file(path, Duration.ofHours(12));
	}

	/**
	 * Returns an instance that prints to log files.
	 *
	 * @param path The root of where the files are output.
	 * @param duration How often it rolls.
	 *
	 * @see Collector#file()
	 * @see Collector#file(Path)
	 * @since 1.0
	 */
	@Contract("null, null -> fail; !null, null -> fail; null, !null -> fail; !null, !null -> new")
	static Collector file(Path path, Duration duration) {
		return new FileCollector(path, duration);
	}
}