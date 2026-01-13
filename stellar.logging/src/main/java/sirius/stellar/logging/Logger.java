package sirius.stellar.logging;

import org.jspecify.annotations.Nullable;
import sirius.stellar.annotation.Contract;
import sirius.stellar.facility.executor.SynchronousExecutorService;
import sirius.stellar.logging.collect.Collector;
import sirius.stellar.logging.dispatch.Dispatcher;
import sirius.stellar.logging.format.LoggerFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

import static java.lang.Runtime.getRuntime;
import static java.util.Collections.synchronizedSet;
import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;
import static java.util.concurrent.TimeUnit.SECONDS;

/// This class is the main entry-point for the logging system.
/// By default, no collectors are registered.
/// See [sirius.stellar.logging] for a usage example.
///
/// ### Dispatch
/// All logging methods (e.g. [Logger#information]) are asynchronous and are
/// executed against a logging executor, which is preferably a platform thread.
///
/// The default executor is the [ForkJoinPool#commonPool()], enough to achieve
/// fast logging performance even over a very involved application.
///
/// Dispatchers that send messages through this logging system can be created,
/// and must delegate messages to [Logger#dispatch], which is an API not for use
/// outside of this domain. The [Dispatcher] SPI can also optionally be
/// leveraged to automatically instantiate your implementations.
///
/// ### Collect
/// Implementations of [Collector]s consume messages on the logging executor,
/// but they are expected to delegate I/O operations required for the usage of
/// the messages to:
///
/// - the [Logger#task] method, which uses a virtual thread executor, and awaits
///   the tasks automatically (so application shutdown does not interrupt them)
///
/// - the [Thread#ofVirtual] method, if the messages do not need to be
///   guaranteed during the shutdown of a given application
///
/// - any other method of dispatching that may already be in-use or shared
///   across an application, if your application shutdown is more advanced (and
///   the provided shutdown hook is insufficient and/or interrupts your I/O
///   too early)
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class Logger extends LoggerMethods {

	private static final ExecutorService virtual = newVirtualThreadPerTaskExecutor();
	private static final LoggerFormatter formatter = LoggerFormatter.create();

	private static Set<Collector> collectors = new HashSet<>();
	private static Set<Future<?>> futures = new HashSet<>();

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

		getRuntime().addShutdownHook(new Thread(() -> {
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

		collectors = synchronizedSet(collectors);
		futures = synchronizedSet(futures);
	}

	/// Dispatch (enqueue) the provided message.
	///
	/// @see LoggerMessage#builder() (creating a message)
	/// @see LoggerMethods (application logging)
	///
	/// @since 1.0
	public static void dispatch(LoggerMessage message) {
		if (executor.isShutdown() || executor.isTerminated()) return;
		futures.add(executor.submit(() -> {
			if (!enabled(message.level())) return;
			if (message.text().isBlank() || message.text().equals("null")) return;
			for (Collector collector : collectors) collector.collect(message);
		}));
	}

	//#region #severity and #enabled*
	/// Set the severity of the logger to the provided value.
	/// If the severity of a message is above this value, it will not be emitted.
	///
	/// @see LoggerLevel
	/// @since 1.0
	public static void severity(int value) {
		if (value < -1) throw new UnsupportedOperationException("Logger severity must be between -1 and " + Integer.MAX_VALUE);
		severity = value;
	}

	/// Returns whether the severity of the logger allows for the provided
	/// level to be logged.
	///
	/// @see Logger#enabled(int)
	/// @since 1.0
	public static boolean enabled(LoggerLevel level) {
		return level.severity() <= severity;
	}

	/// Returns whether the severity of the logger allows for the provided level
	/// (as an integer value) to be logged.
	///
	/// Prefer to use the enumeration based method where possible.
	/// This is provided as a convenience method only.
	///
	/// @see Logger#enabled(LoggerLevel)
	/// @see LoggerLevel#severity()
	/// @since 1.0
	public static boolean enabled(int level) {
		return level <= severity;
	}
	//#endregion

	//#region #executor and #synchronous
	/// Set the [ExecutorService] used by the logger to the provided value.
	/// @since 1.0
	public static void executor(ExecutorService value) {
		if (value.isShutdown() || value.isTerminated()) throw new IllegalArgumentException("Attempted to set executor to a terminated executor");
		executor = value;
	}

	/// Set the [ExecutorService] used by the logger to a [SynchronousExecutorService].
	/// This is a convenience method. It may be undesirable from a performance perspective.
	///
	/// This causes all logging methods to execute on their caller thread.
	/// Thread creation is only used when any registered collectors use [#task].
	///
	/// @since 1.0
	public static void synchronous() {
		executor(new SynchronousExecutorService());
	}
	//#endregion

	//#region #task
	/// Runs a task on a virtual thread.
	///
	/// This task will be awaited for on application shutdown, and should be
	/// used for performing I/O operations for logging, ensuring that those
	/// operations are efficient, but are still cleaned up.
	///
	/// @return The [Future] that returns `null` on completion, which can be
	/// canceled in order to interrupt the dispatched task if necessary.
	public static Future<?> task(Runnable runnable) {
		Future<?> future = virtual.submit(runnable);
		futures.add(future);
		return future;
	}
	//#endregion

	//#region #format*
	/// Returns the provided string, formatted, or `null` if the provided string
	/// is `null`, or if the argument array is `null`.
	///
	/// @see LoggerFormatter
	/// @see #format(Locale, String, Object...)
	/// @since 1.0
	@Nullable
	@Contract("null, _ -> null; _, null -> param1; !null, !null -> new")
	public static String format(@Nullable String string, Object @Nullable ... arguments) {
		if (string == null) return null;
		if (arguments == null) return string;
		return formatter.formatString(string, arguments);
	}

	/// Returns the provided string, formatted, or `null` if the provided string
	/// is `null`, or if the argument array is `null`.
	///
	/// @param locale Locale to use for formatting, or if `null` is provided,
	/// the [#format(String, Object...)] is delegated to instead.
	///
	/// @see LoggerFormatter
	/// @see #format(String, Object...)
	/// @since 1.0
	@Nullable
	@Contract("_, null, _ -> null; _, _, null -> param2; _, !null, !null -> new")
	public static String format(@Nullable Locale locale, @Nullable String string, Object @Nullable ... arguments) {
		if (locale == null) return format(string, arguments);
		if (string == null) return null;
		if (arguments == null) return string;
		return formatter.formatString(locale, string, arguments);
	}
	//#endregion

	//#region #collector*
	/// Registers the provided collector to run when messages are being logged.
	///
	/// Generally, a collector should be an SPI provider.
	/// This can be used instead, for testing purposes or small applications.
	///
	/// @throws UnsupportedOperationException collector already registered
	/// @see #collectors
	/// @since 1.0
	public static void collector(Collector collector) {
		boolean added = collectors.add(collector);
		if (!added) throw new UnsupportedOperationException("Cannot register the same collector twice");
	}

	/// Registers the provided collectors to run when messages are being logged.
	///
	/// This is a delegate of convenience, for [#collector(Collector)], and will
	/// simply invoke that method for each collector in the provided [Iterable].
	///
	/// This method is intended to be used for auto-wiring of collectors through
	/// dependency injection. `List<Collector>` or `Set<Collector>` can be
	/// injected, and provided to this method to register all [Collector]
	/// implementations in the injection graph.
	///
	/// @see #collector
	/// @since 1.0
	public static void collectors(Iterable<Collector> collectors) {
		for (Collector collector : collectors) collector(collector);
	}

	/// Remove/unregister the provided collector, which must match the collector
	/// as either provided to [#collector], or created internally due to the SPI
	/// constructor in [Collector].
	///
	/// The check is performed using [Object#equals], so a collector can be made
	/// removable by making a deterministic implementation of [#equals].
	///
	/// This method should usually not be used.
	/// @since 1.0
	public static void collectorRemove(Collector collector) {
		collectors.remove(collector);
	}
	//#endregion
}