package sirius.stellar.logging;

import org.jspecify.annotations.Nullable;
import sirius.stellar.facility.Strings;
import sirius.stellar.facility.Throwables;
import sirius.stellar.facility.executor.SynchronousExecutorService;
import sirius.stellar.logging.collect.Collector;
import sirius.stellar.logging.dispatch.Dispatcher;
import sirius.stellar.logging.supplier.ObjectSupplier;
import sirius.stellar.logging.supplier.ThrowableSupplier;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static java.lang.Runtime.*;
import static java.lang.StackWalker.Option.*;
import static java.util.concurrent.Executors.*;
import static java.util.concurrent.TimeUnit.*;
import static sirius.stellar.facility.Strings.*;

/// This class is the main entry-point for the logging system.
/// By default, no collectors are registered. See [sirius.stellar.logging] for a usage example.
///
/// All logging methods are asynchronous and executed against a logging executor, which is,
/// preferably, a platform thread. The default executor is the [ForkJoinPool#commonPool()],
/// enough to achieve fast logging performance even over a very involved application.
///
/// Implementations of [Collector]s are expected to use; the provided [Collector#task] method
/// for delegating I/O operations to be performed on virtual threads (such as database writes);
/// the [Thread#ofVirtual] method; any other method of dispatching that may already be in-use or
/// shared across an application.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class Logger {

	private static final List<Collector> collectors = new ArrayList<>();
	private static final Set<Future<?>> futures = new HashSet<>();

	private static final ExecutorService virtual = newVirtualThreadPerTaskExecutor();
	private static final StackWalker walker = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);

	private static int severity = Integer.MAX_VALUE;
	private static ExecutorService executor = ForkJoinPool.commonPool();

	static {
		try {
			ServiceLoader<Dispatcher.Provider> loader = ServiceLoader.load(Dispatcher.Provider.class);
			for (Dispatcher.Provider provider : loader) provider.create().wire();
		} catch (Throwable throwable) {
			throw new IllegalStateException("Failed to wire logging dispatchers", throwable);
		}

		try {
			ServiceLoader<Collector.Provider> loader = ServiceLoader.load(Collector.Provider.class);
			for (Collector.Provider provider : loader) collector(provider.create());
		} catch (ServiceConfigurationError error) {
			throw new IllegalStateException("Failed to wire logging collectors", error);
		}

		getRuntime().addShutdownHook(Thread.ofPlatform().unstarted(() -> {
			try {
				for (Future<?> future : futures) future.get(60, SECONDS);

				if (executor != ForkJoinPool.commonPool()) executor.close();
				for (Collector collector : collectors) collector.close();
				virtual.close();
			} catch (ExecutionException | InterruptedException exception) {
				throw new IllegalStateException("Logging dispatch during shutdown failed or was interrupted", exception);
			} catch (TimeoutException exception) {
				throw new IllegalStateException("Logging dispatch during shutdown timed out", exception);
			}
		}));
	}

	/// Set the severity of the logger to the provided value.
	/// If the severity of a message is above this value, it will not be emitted.
	///
	/// @see LoggerLevel
	/// @since 1.0
	public static void severity(int value) {
		if (value < -1) throw new UnsupportedOperationException("Logger severity must be between -1 and " + Integer.MAX_VALUE);
		severity = value;
	}

	/// Set the [ExecutorService] used by the logger to the provided value.
	/// @since 1.0
	public static void executor(ExecutorService value) {
		if (value.isShutdown() || value.isTerminated()) throw new IllegalArgumentException("Attempted to set executor to a terminated executor");
		executor = value;
	}

	/// Set the [ExecutorService] used by the logger to a [SynchronousExecutorService].
	/// This is a convenience method. It is usually undesirable from a performance perspective.
	///
	/// Calling this method will cause (as a side effect) the logger to no longer cause any given
	/// application to hang/"wait"; there are no non-daemon threads, or any threads for that matter,
	/// created by the logger as a result of this call.
	///
	/// @since 1.0
	public static void synchronous() {
		executor(new SynchronousExecutorService());
	}

	/// Dispatches a message (for use when implementing dispatchers, not for
	/// application logging).
	///
	/// @param thread The name of the thread this message was dispatched from.
	/// This should never be the identifier of the thread [Thread#threadId()].
	///
	/// @param name The original caller that caused this dispatch. This is retrieved
	/// quickly with each call using [StackWalker#getCallerClass()].
	///
	/// @param arguments Arguments to use for string interpolation / formatting.
	/// This invokes [Strings#format(String,Object...)], essentially using both the
	/// [java.text.MessageFormat] and [String#format] styles of formatting.
	///
	/// When making dispatchers, this should be avoided and the specific style of
	/// interpolation used by the specific logger or facade that the dispatcher is to
	/// delegate should be called instead, and this argument should be `null`.
	public static void dispatch(Instant time, LoggerLevel level, String thread, String name,
								@Nullable String text, Object @Nullable... arguments) {
		if (executor.isShutdown() || executor.isTerminated()) return;
		futures.add(executor.submit(() -> {
			if (!enabled(level)) return;
			if (text == null || text.isBlank() || text.equalsIgnoreCase("null")) return;

			LoggerMessage message = LoggerMessage.builder()
					.time(time)
					.level(level)
					.thread(thread)
					.name(name)
					.text((arguments == null || arguments.length == 0) ? text : format(text, arguments))
					.build();
			collectors.forEach(collector -> collector.collect(message));
		}));
	}

	/// Dispatches a task on a virtual thread.
	///
	/// This task will be awaited for on application shutdown, and should be used
	/// for performing I/O operations for logging, ensuring that those operations
	/// are efficient, but are still cleaned up.
	///
	/// @return The [Future] that returns `null` on completion, which can be
	/// canceled in order to interrupt the dispatched task if necessary.
	public static Future<?> task(Runnable runnable) {
		Future<?> future = virtual.submit(runnable);
		futures.add(future);
		return future;
	}

	//#region enabled*
	/// Returns whether the severity of the logger allows for the provided level to be logged.
	///
	/// @see Logger#enabled(int)
	/// @since 1.0
	public static boolean enabled(LoggerLevel level) {
		return level.severity() <= severity;
	}

	/// Returns whether the severity of the logger allows for the provided integer value level to be logged.
	/// Prefer to use the enumerator based method where possible. This is provided as a convenience method only.
	///
	/// @see Logger#enabled(LoggerLevel)
	/// @see LoggerLevel#severity()
	/// @since 1.0
	public static boolean enabled(int level) {
		return level <= severity;
	}
	//#endregion

	//#region collector*
	/// Registers the provided collector to run when things are being logged.
	///
	/// @see #collectors
	/// @since 1.0
	public static void collector(Collector collector) {
		if (collectors.contains(collector)) throw new UnsupportedOperationException("Cannot register the same collector twice");
		collectors.add(collector);
	}

	/// Registers the provided collectors to run when things are being logged.
	///
	/// This method is intended to be used for auto-wiring of collectors through dependency injection.
	/// `List<Collector>` or `Set<Collector>` can be injected and provided to this method to register
	/// every [Collector] implementation available.
	///
	/// @see #collector
	/// @since 1.0
	public static void collectors(Iterable<Collector> collectors) {
		collectors.forEach(Logger::collector);
	}
	//#endregion

	//#region Logging [information*]
	/**
	 * Logs a message at {@link LoggerLevel#INFORMATION}.
	 * This accepts no objects for formatting to prevent the creation of an array.
	 *
	 * @since 1.0
	 */
	public static void information(String text) {
		if (!enabled(LoggerLevel.INFORMATION)) return;
		dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), text);
	}

	/**
	 * Logs a message at {@link LoggerLevel#INFORMATION}.
	 * This uses {@link String#valueOf(Object)} only if the logger is enabled at this level.
	 *
	 * @since 1.0
	 */
	public static void information(Object object) {
		if (!enabled(LoggerLevel.INFORMATION)) return;
		dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(object));
	}

	/**
	 * Logs a message at {@link LoggerLevel#INFORMATION}.
	 * This accepts a single object as an argument for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void information(String text, Object argument) {
		if (!enabled(LoggerLevel.INFORMATION)) return;
		dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument));
	}

	/**
	 * Logs a message at {@link LoggerLevel#INFORMATION}.
	 * This accepts two objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void information(String text, Object argument1, Object argument2) {
		if (!enabled(LoggerLevel.INFORMATION)) return;
		dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2));
	}

	/**
	 * Logs a message at {@link LoggerLevel#INFORMATION}.
	 * This accepts three objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void information(String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(LoggerLevel.INFORMATION)) return;
		dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2, argument3));
	}

	/**
	 * Logs a message at {@link LoggerLevel#INFORMATION}.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void information(String text, Object... arguments) {
		if (!enabled(LoggerLevel.INFORMATION)) return;
		dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, arguments));
	}
	//#endregion
	//#region Logging [information*, Lambda]
	/**
	 * Logs a message at {@link LoggerLevel#INFORMATION}.
	 * This uses {@link String#valueOf(Object)} only if the logger is enabled at this level.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 *
	 * @since 1.0
	 */
	public static void information(@Nullable ObjectSupplier supplier) {
		if (!enabled(LoggerLevel.INFORMATION)) return;
		if (supplier == null) return;
		dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(supplier.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#INFORMATION}.
	 * This accepts a single object supplier as an argument for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void information(String text, @Nullable ObjectSupplier argument) {
		if (!enabled(LoggerLevel.INFORMATION)) return;
		if (argument == null) return;
		dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#INFORMATION}.
	 * This accepts two object suppliers as arguments for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void information(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2) {
		if (!enabled(LoggerLevel.INFORMATION)) return;
		if (argument1 == null || argument2 == null) return;
		dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#INFORMATION}.
	 * This accepts three object suppliers as arguments for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void information(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2, @Nullable ObjectSupplier argument3) {
		if (!enabled(LoggerLevel.INFORMATION)) return;
		if (argument1 == null || argument2 == null || argument3 == null) return;
		dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get(), argument3.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#INFORMATION}.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void information(String text, ObjectSupplier @Nullable... arguments) {
		if (!enabled(LoggerLevel.INFORMATION)) return;
		if (arguments == null) return;
		dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, Arrays.stream(arguments).map(Supplier::get).toArray()));
	}
	//#endregion

	//#region Logging [warning*]
	/**
	 * Logs a message at {@link LoggerLevel#WARNING}.
	 * This accepts no objects for formatting to prevent the creation of an array.
	 *
	 * @since 1.0
	 */
	public static void warning(String text) {
		if (!enabled(LoggerLevel.WARNING)) return;
		dispatch(Instant.now(), LoggerLevel.WARNING, Thread.currentThread().getName(), walker.getCallerClass().getName(), text);
	}

	/**
	 * Logs a message at {@link LoggerLevel#WARNING}.
	 * This uses {@link String#valueOf(Object)} only if the logger is enabled at this level.
	 *
	 * @since 1.0
	 */
	public static void warning(Object object) {
		if (!enabled(LoggerLevel.WARNING)) return;
		dispatch(Instant.now(), LoggerLevel.WARNING, Thread.currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(object));
	}

	/**
	 * Logs a message at {@link LoggerLevel#WARNING}.
	 * This accepts a single object as an argument for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void warning(String text, Object argument) {
		if (!enabled(LoggerLevel.WARNING)) return;
		dispatch(Instant.now(), LoggerLevel.WARNING, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument));
	}

	/**
	 * Logs a message at {@link LoggerLevel#WARNING}.
	 * This accepts two objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void warning(String text, Object argument1, Object argument2) {
		if (!enabled(LoggerLevel.WARNING)) return;
		dispatch(Instant.now(), LoggerLevel.WARNING, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2));
	}

	/**
	 * Logs a message at {@link LoggerLevel#WARNING}.
	 * This accepts three objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void warning(String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(LoggerLevel.WARNING)) return;
		dispatch(Instant.now(), LoggerLevel.WARNING, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2, argument3));
	}

	/**
	 * Logs a message at {@link LoggerLevel#WARNING}.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void warning(String text, Object... arguments) {
		if (!enabled(LoggerLevel.WARNING)) return;
		dispatch(Instant.now(), LoggerLevel.WARNING, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, arguments));
	}
	//#endregion
	//#region Logging [warning*, Lambda]
	/**
	 * Logs a message at {@link LoggerLevel#WARNING}.
	 * This uses {@link String#valueOf(Object)} only if the logger is enabled at this level.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 *
	 * @since 1.0
	 */
	public static void warning(@Nullable ObjectSupplier supplier) {
		if (!enabled(LoggerLevel.WARNING)) return;
		if (supplier == null) return;
		dispatch(Instant.now(), LoggerLevel.WARNING, Thread.currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(supplier.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#WARNING}.
	 * This accepts a single object supplier as an argument for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void warning(String text, @Nullable ObjectSupplier argument) {
		if (!enabled(LoggerLevel.WARNING)) return;
		if (argument == null) return;
		dispatch(Instant.now(), LoggerLevel.WARNING, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#WARNING}.
	 * This accepts two object suppliers as arguments for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void warning(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2) {
		if (!enabled(LoggerLevel.WARNING)) return;
		if (argument1 == null || argument2 == null) return;
		dispatch(Instant.now(), LoggerLevel.WARNING, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#WARNING}.
	 * This accepts three object suppliers as arguments for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void warning(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2, @Nullable ObjectSupplier argument3) {
		if (!enabled(LoggerLevel.WARNING)) return;
		if (argument1 == null || argument2 == null || argument3 == null) return;
		dispatch(Instant.now(), LoggerLevel.WARNING, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get(), argument3.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#WARNING}.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void warning(String text, ObjectSupplier @Nullable... arguments) {
		if (!enabled(LoggerLevel.WARNING)) return;
		if (arguments == null) return;
		dispatch(Instant.now(), LoggerLevel.WARNING, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, Arrays.stream(arguments).map(Supplier::get).toArray()));
	}
	//#endregion

	//#region Logging [error*]
	/**
	 * Logs a message at {@link LoggerLevel#ERROR}.
	 * This accepts no objects for formatting to prevent the creation of an array.
	 *
	 * @since 1.0
	 */
	public static void error(String text) {
		if (!enabled(LoggerLevel.ERROR)) return;
		dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), walker.getCallerClass().getName(), text);
	}

	/**
	 * Logs a message at {@link LoggerLevel#ERROR}.
	 * This uses {@link String#valueOf(Object)} only if the logger is enabled at this level.
	 *
	 * @since 1.0
	 */
	public static void error(Object object) {
		if (!enabled(LoggerLevel.ERROR)) return;
		dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(object));
	}

	/**
	 * Logs a message at {@link LoggerLevel#ERROR}.
	 * This accepts a single object as an argument for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void error(String text, Object argument) {
		if (!enabled(LoggerLevel.ERROR)) return;
		dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument));
	}

	/**
	 * Logs a message at {@link LoggerLevel#ERROR}.
	 * This accepts two objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void error(String text, Object argument1, Object argument2) {
		if (!enabled(LoggerLevel.ERROR)) return;
		dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2));
	}

	/**
	 * Logs a message at {@link LoggerLevel#ERROR}.
	 * This accepts three objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void error(String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(LoggerLevel.ERROR)) return;
		dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2, argument3));
	}

	/**
	 * Logs a message at {@link LoggerLevel#ERROR}.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void error(String text, Object... arguments) {
		if (!enabled(LoggerLevel.ERROR)) return;
		dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, arguments));
	}
	//#endregion
	//#region Logging [error*, Lambda]
	/**
	 * Logs a message at {@link LoggerLevel#ERROR}.
	 * This uses {@link String#valueOf(Object)} only if the logger is enabled at this level.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 *
	 * @since 1.0
	 */
	public static void error(@Nullable ObjectSupplier supplier) {
		if (!enabled(LoggerLevel.ERROR)) return;
		if (supplier == null) return;
		dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(supplier.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#ERROR}.
	 * This accepts a single object supplier as an argument for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void error(String text, @Nullable ObjectSupplier argument) {
		if (!enabled(LoggerLevel.ERROR)) return;
		if (argument == null) return;
		dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#ERROR}.
	 * This accepts two object suppliers as arguments for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void error(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2) {
		if (!enabled(LoggerLevel.ERROR)) return;
		if (argument1 == null || argument2 == null) return;
		dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#ERROR}.
	 * This accepts three object suppliers as arguments for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void error(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2, @Nullable ObjectSupplier argument3) {
		if (!enabled(LoggerLevel.ERROR)) return;
		if (argument1 == null || argument2 == null || argument3 == null) return;
		dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get(), argument3.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#ERROR}.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void error(String text, ObjectSupplier @Nullable... arguments) {
		if (!enabled(LoggerLevel.ERROR)) return;
		if (arguments == null) return;
		dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, Arrays.stream(arguments).map(Supplier::get).toArray()));
	}
	//#endregion

	//#region Logging [stacktrace*]
	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts no objects for formatting to prevent the creation of an array.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(String text) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), text);
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This uses {@link String#valueOf(Object)} only if the logger is enabled at this level.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(Object object) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(object));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts a single object as an argument for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(String text, Object argument) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts two objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(String text, Object argument1, Object argument2) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts three objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2, argument3));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(String text, Object... arguments) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, arguments));
	}
	//#endregion
	//#region Logging [stacktrace*, Throwable]
	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * The stacktrace for the provided {@link Throwable} is printed
	 * out only if the logger is enabled at this level.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(Throwable throwable) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), Throwables.stacktrace(throwable));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts no objects for formatting to prevent the creation of an array.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(Throwable throwable, String text) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + Throwables.stacktrace(throwable);
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), text);
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This uses {@link String#valueOf(Object)} only if the logger is enabled at this level.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(Throwable throwable, Object object) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		String text = object + "\n" + Throwables.stacktrace(throwable);
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), text);
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts a single object as an argument for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(Throwable throwable, String text, Object argument) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + Throwables.stacktrace(throwable);
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts two objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(Throwable throwable, String text, Object argument1, Object argument2) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + Throwables.stacktrace(throwable);
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts three objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(Throwable throwable, String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + Throwables.stacktrace(throwable);
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2, argument3));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(Throwable throwable, String text, Object... arguments) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + Throwables.stacktrace(throwable);
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, arguments));
	}
	//#endregion
	//#region Logging [stacktrace*, Lambda for formatting]
	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This uses {@link String#valueOf(Object)} only if the logger is enabled at this level.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(@Nullable ObjectSupplier supplier) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		if (supplier == null) return;
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(supplier.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts a single object supplier as an argument for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(String text, @Nullable ObjectSupplier argument) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		if (argument == null) return;
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts two object suppliers as arguments for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		if (argument1 == null || argument2 == null) return;
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts three object suppliers as arguments for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2, @Nullable ObjectSupplier argument3) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		if (argument1 == null || argument2 == null || argument3 == null) return;
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get(), argument3.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(String text, ObjectSupplier @Nullable... arguments) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		if (arguments == null) return;
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, Arrays.stream(arguments).map(Supplier::get).toArray()));
	}
	//#endregion
	//#region Logging [stacktrace*, Lambda for throwable]
	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * The stacktrace for the provided {@link Throwable} is printed
	 * out only if the logger is enabled at this level.
	 * <p>
	 * A supplier is used on this method to allow for the throwable not to be evaluated (and stored), useful for if
	 * obtaining an instance of the throwable is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Obtaining an instance of a throwable can be a heavy operation if evaluating the arguments to the constructor of
	 * the throwable is a heavy operation, e.g., a call to {@link Strings#format(String, Object...)} for building the
	 * message, supplying arguments that are heavy to evaluate. This is why the lambda pattern is ideal for this type
	 * of scenario when making debug records in the log.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(ThrowableSupplier throwable) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), Throwables.stacktrace(throwable.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts no objects for formatting to prevent the creation of an array.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 * <p>
	 * A supplier is used on this method to allow for the throwable not to be evaluated (and stored), useful for if
	 * obtaining an instance of the throwable is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Obtaining an instance of a throwable can be a heavy operation if evaluating the arguments to the constructor of
	 * the throwable is a heavy operation, e.g., a call to {@link Strings#format(String, Object...)} for building the
	 * message, supplying arguments that are heavy to evaluate. This is why the lambda pattern is ideal for this type
	 * of scenario when making debug records in the log.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(ThrowableSupplier throwable, String text) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + Throwables.stacktrace(throwable.get());
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), text);
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This uses {@link String#valueOf(Object)} only if the logger is enabled at this level.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 * <p>
	 * A supplier is used on this method to allow for the throwable not to be evaluated (and stored), useful for if
	 * obtaining an instance of the throwable is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Obtaining an instance of a throwable can be a heavy operation if evaluating the arguments to the constructor of
	 * the throwable is a heavy operation, e.g., a call to {@link Strings#format(String, Object...)} for building the
	 * message, supplying arguments that are heavy to evaluate. This is why the lambda pattern is ideal for this type
	 * of scenario when making debug records in the log.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(ThrowableSupplier throwable, Object object) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		String text = object + "\n" + Throwables.stacktrace(throwable.get());
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), text);
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts a single object as an argument for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 * <p>
	 * A supplier is used on this method to allow for the throwable not to be evaluated (and stored), useful for if
	 * obtaining an instance of the throwable is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Obtaining an instance of a throwable can be a heavy operation if evaluating the arguments to the constructor of
	 * the throwable is a heavy operation, e.g., a call to {@link Strings#format(String, Object...)} for building the
	 * message, supplying arguments that are heavy to evaluate. This is why the lambda pattern is ideal for this type
	 * of scenario when making debug records in the log.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(ThrowableSupplier throwable, String text, Object argument) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + Throwables.stacktrace(throwable.get());
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts two objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 * <p>
	 * A supplier is used on this method to allow for the throwable not to be evaluated (and stored), useful for if
	 * obtaining an instance of the throwable is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Obtaining an instance of a throwable can be a heavy operation if evaluating the arguments to the constructor of
	 * the throwable is a heavy operation, e.g., a call to {@link Strings#format(String, Object...)} for building the
	 * message, supplying arguments that are heavy to evaluate. This is why the lambda pattern is ideal for this type
	 * of scenario when making debug records in the log.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(ThrowableSupplier throwable, String text, Object argument1, Object argument2) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + Throwables.stacktrace(throwable.get());
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts three objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 * <p>
	 * A supplier is used on this method to allow for the throwable not to be evaluated (and stored), useful for if
	 * obtaining an instance of the throwable is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Obtaining an instance of a throwable can be a heavy operation if evaluating the arguments to the constructor of
	 * the throwable is a heavy operation, e.g., a call to {@link Strings#format(String, Object...)} for building the
	 * message, supplying arguments that are heavy to evaluate. This is why the lambda pattern is ideal for this type
	 * of scenario when making debug records in the log.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(ThrowableSupplier throwable, String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + Throwables.stacktrace(throwable.get());
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2, argument3));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 * <p>
	 * A supplier is used on this method to allow for the throwable not to be evaluated (and stored), useful for if
	 * obtaining an instance of the throwable is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Obtaining an instance of a throwable can be a heavy operation if evaluating the arguments to the constructor of
	 * the throwable is a heavy operation, e.g., a call to {@link Strings#format(String, Object...)} for building the
	 * message, supplying arguments that are heavy to evaluate. This is why the lambda pattern is ideal for this type
	 * of scenario when making debug records in the log.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(ThrowableSupplier throwable, String text, Object... arguments) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + Throwables.stacktrace(throwable.get());
		dispatch(Instant.now(), LoggerLevel.STACKTRACE, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, arguments));
	}
	//#endregion

	//#region Logging [debugging*]
	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * This accepts no objects for formatting to prevent the creation of an array.
	 *
	 * @since 1.0
	 */
	public static void debugging(String text) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		dispatch(Instant.now(), LoggerLevel.DEBUGGING, Thread.currentThread().getName(), walker.getCallerClass().getName(), text);
	}

	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * This uses {@link String#valueOf(Object)} only if the logger is enabled at this level.
	 *
	 * @since 1.0
	 */
	public static void debugging(Object object) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		dispatch(Instant.now(), LoggerLevel.DEBUGGING, Thread.currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(object));
	}

	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * This accepts a single object as an argument for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void debugging(String text, Object argument) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		dispatch(Instant.now(), LoggerLevel.DEBUGGING, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument));
	}

	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * This accepts two objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void debugging(String text, Object argument1, Object argument2) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		dispatch(Instant.now(), LoggerLevel.DEBUGGING, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2));
	}

	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * This accepts three objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void debugging(String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		dispatch(Instant.now(), LoggerLevel.DEBUGGING, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2, argument3));
	}

	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void debugging(String text, Object... arguments) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		dispatch(Instant.now(), LoggerLevel.DEBUGGING, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, arguments));
	}
	//#endregion
	//#region Logging [debugging*, Lambda]
	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * This uses {@link String#valueOf(Object)} only if the logger is enabled at this level.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 *
	 * @since 1.0
	 */
	public static void debugging(@Nullable ObjectSupplier supplier) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		if (supplier == null) return;
		dispatch(Instant.now(), LoggerLevel.DEBUGGING, Thread.currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(supplier.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * This accepts a single object supplier as an argument for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void debugging(String text, @Nullable ObjectSupplier argument) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		if (argument == null) return;
		dispatch(Instant.now(), LoggerLevel.DEBUGGING, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * This accepts two object suppliers as arguments for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void debugging(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		if (argument1 == null || argument2 == null) return;
		dispatch(Instant.now(), LoggerLevel.DEBUGGING, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * This accepts three object suppliers as arguments for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void debugging(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2, @Nullable ObjectSupplier argument3) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		if (argument1 == null || argument2 == null || argument3 == null) return;
		dispatch(Instant.now(), LoggerLevel.DEBUGGING, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get(), argument3.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void debugging(String text, ObjectSupplier @Nullable... arguments) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		if (arguments == null) return;
		dispatch(Instant.now(), LoggerLevel.DEBUGGING, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, Arrays.stream(arguments).map(Supplier::get).toArray()));
	}
	//#endregion

	//#region Logging [configuration*]
	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * This accepts no objects for formatting to prevent the creation of an array.
	 *
	 * @since 1.0
	 */
	public static void configuration(String text) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		dispatch(Instant.now(), LoggerLevel.CONFIGURATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), text);
	}

	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * This uses {@link String#valueOf(Object)} only if the logger is enabled at this level.
	 *
	 * @since 1.0
	 */
	public static void configuration(Object object) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		dispatch(Instant.now(), LoggerLevel.CONFIGURATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(object));
	}

	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * This accepts a single object as an argument for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void configuration(String text, Object argument) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		dispatch(Instant.now(), LoggerLevel.CONFIGURATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument));
	}

	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * This accepts two objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void configuration(String text, Object argument1, Object argument2) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		dispatch(Instant.now(), LoggerLevel.CONFIGURATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2));
	}

	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * This accepts three objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void configuration(String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		dispatch(Instant.now(), LoggerLevel.CONFIGURATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2, argument3));
	}

	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void configuration(String text, Object... arguments) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		dispatch(Instant.now(), LoggerLevel.CONFIGURATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, arguments));
	}
	//#endregion
	//#region Logging [configuration*, Lambda]
	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * This uses {@link String#valueOf(Object)} only if the logger is enabled at this level.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 *
	 * @since 1.0
	 */
	public static void configuration(@Nullable ObjectSupplier supplier) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		if (supplier == null) return;
		dispatch(Instant.now(), LoggerLevel.CONFIGURATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(supplier.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * This accepts a single object supplier as an argument for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void configuration(String text, @Nullable ObjectSupplier argument) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		if (argument == null) return;
		dispatch(Instant.now(), LoggerLevel.CONFIGURATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * This accepts two object suppliers as arguments for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void configuration(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		if (argument1 == null || argument2 == null) return;
		dispatch(Instant.now(), LoggerLevel.CONFIGURATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * This accepts three object suppliers as arguments for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void configuration(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2, @Nullable ObjectSupplier argument3) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		if (argument1 == null || argument2 == null || argument3 == null) return;
		dispatch(Instant.now(), LoggerLevel.CONFIGURATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get(), argument3.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Strings#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void configuration(String text, ObjectSupplier @Nullable... arguments) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		if (arguments == null) return;
		dispatch(Instant.now(), LoggerLevel.CONFIGURATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, Arrays.stream(arguments).map(Supplier::get).toArray()));
	}
	//#endregion
}