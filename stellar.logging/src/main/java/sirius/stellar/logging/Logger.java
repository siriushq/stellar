package sirius.stellar.logging;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullUnmarked;
import sirius.stellar.facility.Strings;
import sirius.stellar.facility.Throwables;
import sirius.stellar.facility.doctation.Internal;
import sirius.stellar.facility.executor.SynchronousExecutorService;
import sirius.stellar.logging.collect.Collector;
import sirius.stellar.logging.dispatch.Dispatcher;
import sirius.stellar.logging.supplier.ObjectSupplier;
import sirius.stellar.logging.supplier.ThrowableSupplier;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static sirius.stellar.facility.Strings.*;

/**
 * This class is the main entry-point for the logging system.
 * <p>
 * If the framework is not being used in full (and this logging system is being used as a standalone dependency), a given
 * user might want to run {@code Logger.collector(Collector.console())} to ensure logs are appended into the console and
 * {@link System#out} and {@link System#err} are overridden. This is not called by default to allow for full flexibility.
 * <p>
 * All logging methods are asynchronous and executed against a single logging thread (a platform thread). Virtual
 * threads are not used in this case (in favor of {@code Executors.newFixedThreadPool(1)}) as only one single thread
 * is needed to achieve fast logging performance even over a very involved application. This can be changed with
 * {@link Logger#executor(ExecutorService)} if you wish to register collectors that will perform very heavy lifting,
 * such as database writes or submitting to an external message broker, or any other kind of IO operation.
 * <p>
 * The preferred alternative to the default if this is required is {@link Executors#newVirtualThreadPerTaskExecutor()},
 * however, if a fixed number of total virtual threads is desired (which is generally an anti-pattern, as virtual threads
 * are extremely lightweight to create and the number of carrier threads is bound to the number of CPU cores available -
 * but this can be useful if you only want one logging thread, but you want a virtual thread so, if it blocks, other more
 * important business-logic threads can process instead), {@code newFixedThreadPool(1, Thread::startVirtualThread)} can be
 * used to achieve that.
 * <p>
 * Otherwise, it is also a valid option to use any executor you might be sharing across the application for this purpose, and
 * it could benefit the performance of your business logic to use a virtual thread executor as if you are using virtual threads
 * elsewhere, they will complement each other when any heavy IO takes place and free up the carrier threads for more work to be
 * performed, while also allowing for a low amount of memory to be consumed due to the lack of actual platform threads.
 *
 * @since 1.0
 * @author Mechite
 */
@NullUnmarked
public final class Logger {

	private static final List<Collector> collectors = new ArrayList<>();
	private static final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

	private static int severity = Integer.MAX_VALUE;
	private static ExecutorService executor = Executors.newFixedThreadPool(1);

	static {
		try {
			ServiceLoader<Dispatcher.Provider> loader = ServiceLoader.load(Dispatcher.Provider.class);
			for (Dispatcher.Provider provider : loader) provider.create().wire();
		} catch (Throwable throwable) {
			Logger.stacktrace("Failed to wire dispatchers", throwable);
		}
	}

	/**
	 * Set the severity of the logger to the provided value.
	 * If the severity of a message is above this value, it will not be emitted.
	 *
	 * @see LoggerLevel
	 * @since 1.0
	 */
	public static void severity(int value) {
		if (value < 0) throw new UnsupportedOperationException("Logger severity must be between 0 and Integer#MAX_VALUE");
		severity = value;
	}

	/**
	 * Set the {@link ExecutorService} used by the logger to the provided value.
	 * @since 1.0
	 */
	public static void executor(ExecutorService value) {
		if (value == null) throw new NullPointerException("Attempted to set executor to null");
		if (value.isShutdown() || value.isTerminated()) throw new UnsupportedOperationException("Attempted to set executor to a terminated executor");
		executor = value;
	}

	/**
	 * Set the {@link ExecutorService} used by the logger to a {@link SynchronousExecutorService}.
	 * <p>
	 * This is mostly a convenience method, but a different approach may be used in the future to
	 * provide a fast synchronous logger such as an event loop or more advanced collection system
	 * in order to achieve optimal performance. This is why this method should be preferred instead
	 * of calling {@code Logger.executor(new SynchronousExecutorService())} directly.
	 * <p>
	 * Calling this method will cause (as a side effect) the logger to no longer cause any given
	 * application to hang/"wait"; there are no non-daemon threads, or any threads for that matter,
	 * created by the logger as a result of this call.
	 *
	 * @since 1.0
	 */
	public static void synchronous() {
		executor(new SynchronousExecutorService());
	}

	/**
	 * Dispatches a message.
	 *
	 * @param thread The name of the thread this message was dispatched from.
	 * This should never be the identifier of the thread {@link Thread#threadId()}.
	 *
	 * @param name The original caller that caused this dispatch. This is retrieved
	 * quickly with each call using {@link StackWalker#getCallerClass()}.
	 *
	 * @param arguments Arguments to use for string interpolation / formatting. This
	 * invokes {@link Strings#format(String, Object...)}, essentially using both the
	 * {@link java.text.MessageFormat} and {@link String#format} styles of formatting.
	 * <p>
	 * When making dispatchers, this should be avoided and the specific style of
	 * interpolation used by the specific logger or facade that the dispatcher is to
	 * delegate should be called instead, and this argument should be null.
	 */
	@Internal
	public static void dispatch(Instant time, LoggerLevel level, String thread, String name, String text, Object... arguments) {
		if (executor.isShutdown() || executor.isTerminated()) return;
		executor.submit(() -> {
			if (!enabled(level)) return;
			if (text == null || text.isEmpty() || text.isBlank() || text.equalsIgnoreCase("null")) return;

			LoggerMessage message = new LoggerMessage(time, level, thread, name, (arguments == null || arguments.length == 0) ? text : format(text, arguments));
			collectors.forEach(collector -> collector.collect(message));
		});
	}

	/**
	 * Closes the logger.
	 * <p>
	 * This prevents any new messages being dispatched. This call is irreversible, and if
	 * the framework is used in full, it is automatically bound to a shutdown hook to reduce
	 * boilerplate as this is, very often, the desired approach to handling the closure of
	 * the logging system.
	 * <p>
	 * However, it could be noted that many collectors often have dependencies such as external
	 * message brokers whose clients need to be gracefully closed after the logger. This is why
	 * {@link Collector#close()} is run for all collectors before the closure of the logging
	 * system; handling can be added to guarantee that collectors will never fail. Furthermore,
	 * it is extremely important that there is at least one active collector publishing logging
	 * in a production application; information about the shutdown of an application can be very
	 * helpful to diagnose data loss issues, even when replication is an enforced practice.
	 * <p>
	 * Logging tends to be the last thing that closes in an application - undesirable behavior,
	 * as issues can occur if the dependencies a collector might have are longer accepting calls.
	 */
	public static void close() {
		try {
			executor.shutdown();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			executor.close();

			for (Collector collector : collectors) collector.close();

			Collector.executor.shutdown();
			Collector.executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			Collector.executor.close();
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	//#region enabled*
	/**
	 * Returns whether the severity of the logger allows for the provided level to be logged.
	 *
	 * @see Logger#enabled(int)
	 * @since 1.0
	 */
	@Contract("null -> fail")
	public static boolean enabled(LoggerLevel level) {
		return level.severity() < severity;
	}

	/**
	 * Returns whether the severity of the logger allows for the provided integer value level to be logged.
	 * Prefer to use the enumerator based method where possible. This is provided as a convenience method only.
	 *
	 * @see Logger#enabled(LoggerLevel)
	 * @see LoggerLevel#severity()
	 * @since 1.0
	 */
	public static boolean enabled(int level) {
		return level < severity;
	}
	//#endregion

	//#region collector*
	/**
	 * Registers the provided collector to run when things are being logged.
	 * @since 1.0
	 */
	public static void collector(Collector collector) {
		if (collectors.contains(collector)) throw new UnsupportedOperationException("Cannot register the same collector twice");
		collectors.add(collector);
	}

	/**
	 * Registers the provided collectors to run when things are being logged.
	 * This method is intended to be used for the separation of collectors through dependency injection.
	 * <p>
	 * A {@code List<Collector>} or {@code Set<Collector>} can be accepted as a dependency / injected,
	 * and then passed through to this method to register all the collectors. This allows for every file
	 * to have a separate implementation of {@link Collector} only with dependencies on what is needed
	 * for the collection, allowing for collectors to be safely tested.
	 *
	 * @since 1.0
	 */
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
	public static void information(ObjectSupplier supplier) {
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
	public static void information(String text, ObjectSupplier argument) {
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
	public static void information(String text, ObjectSupplier argument1, ObjectSupplier argument2) {
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
	public static void information(String text, ObjectSupplier argument1, ObjectSupplier argument2, ObjectSupplier argument3) {
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
	public static void information(String text, ObjectSupplier... arguments) {
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
	public static void warning(ObjectSupplier supplier) {
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
	public static void warning(String text, ObjectSupplier argument) {
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
	public static void warning(String text, ObjectSupplier argument1, ObjectSupplier argument2) {
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
	public static void warning(String text, ObjectSupplier argument1, ObjectSupplier argument2, ObjectSupplier argument3) {
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
	public static void warning(String text, ObjectSupplier... arguments) {
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
	public static void error(ObjectSupplier supplier) {
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
	public static void error(String text, ObjectSupplier argument) {
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
	public static void error(String text, ObjectSupplier argument1, ObjectSupplier argument2) {
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
	public static void error(String text, ObjectSupplier argument1, ObjectSupplier argument2, ObjectSupplier argument3) {
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
	public static void error(String text, ObjectSupplier... arguments) {
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
	public static void stacktrace(ObjectSupplier supplier) {
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
	public static void stacktrace(String text, ObjectSupplier argument) {
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
	public static void stacktrace(String text, ObjectSupplier argument1, ObjectSupplier argument2) {
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
	public static void stacktrace(String text, ObjectSupplier argument1, ObjectSupplier argument2, ObjectSupplier argument3) {
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
	public static void stacktrace(String text, ObjectSupplier... arguments) {
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
	public static void debugging(ObjectSupplier supplier) {
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
	public static void debugging(String text, ObjectSupplier argument) {
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
	public static void debugging(String text, ObjectSupplier argument1, ObjectSupplier argument2) {
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
	public static void debugging(String text, ObjectSupplier argument1, ObjectSupplier argument2, ObjectSupplier argument3) {
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
	public static void debugging(String text, ObjectSupplier... arguments) {
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
	public static void configuration(ObjectSupplier supplier) {
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
	public static void configuration(String text, ObjectSupplier argument) {
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
	public static void configuration(String text, ObjectSupplier argument1, ObjectSupplier argument2) {
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
	public static void configuration(String text, ObjectSupplier argument1, ObjectSupplier argument2, ObjectSupplier argument3) {
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
	public static void configuration(String text, ObjectSupplier... arguments) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		if (arguments == null) return;
		dispatch(Instant.now(), LoggerLevel.CONFIGURATION, Thread.currentThread().getName(), walker.getCallerClass().getName(), format(text, Arrays.stream(arguments).map(Supplier::get).toArray()));
	}
	//#endregion
}