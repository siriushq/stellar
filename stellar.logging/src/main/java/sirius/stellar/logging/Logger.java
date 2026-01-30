package sirius.stellar.logging;

import org.jspecify.annotations.Nullable;
import sirius.stellar.annotation.Contract;
import sirius.stellar.logging.concurrent.LoggerScheduler;
import sirius.stellar.logging.format.LoggerFormatter;
import sirius.stellar.logging.spi.LoggerCollector;
import sirius.stellar.logging.spi.LoggerDispatcher;
import sirius.stellar.logging.spi.LoggerExtension;

import java.util.Locale;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.*;

import static java.lang.Runtime.getRuntime;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.onSpinWait;
import static java.util.ServiceLoader.load;
import static java.util.concurrent.Executors.callable;
import static java.util.concurrent.locks.LockSupport.parkNanos;

/// This class is the main entry-point for the logging system.
///
/// ### Dispatch
/// Dispatchers that send messages through this logging system, e.g. for
/// delegating other logging facades through it, can be created by implementing
/// the [LoggerDispatcher] interface, and optionally implementations can be
/// provided as [LoggerExtension] SPI providers.
///
/// All logging methods (e.g. [Logger#information]) dispatch the same way, and
/// are provided as a logging API / facade, for application or library logging.
///
/// ### Collect
/// Collectors that consume messages from this logging system, e.g. for sending
/// to custom destinations, can be created by implementing the [LoggerCollector]
/// interface, and either registered with e.g. [#collector], or by being
/// provided as [LoggerExtension] SPI providers.
///
/// @since 1.0
public final class Logger extends LoggerMethods {

	private static final LoggerFormatter formatter = LoggerFormatter.create();
	private static final LoggerScheduler scheduler = LoggerScheduler.create();

	private static final BlockingDeque<LoggerMessage> deque = new LinkedBlockingDeque<>();
	private static final Set<LoggerCollector> collectors = ConcurrentHashMap.newKeySet();

	private static volatile boolean closing = false;
	private static int severity = Integer.MAX_VALUE;

	static {
		try {
			ServiceLoader<LoggerExtension> loader = load(LoggerExtension.class);
			for (LoggerExtension extension : loader) extension.wire();
		} catch (Throwable throwable) {
			throw new IllegalStateException("Failed to wire logger extensions", throwable);
		}

		scheduler.execute(Logger::poll);
		getRuntime().addShutdownHook(new Thread(Logger::close));
	}

	/// Repeatedly visit the queue, blocking for the next message, then submit
	/// to all collectors for consumption, until the thread is interrupted.
	private static void poll() {
		while (!currentThread().isInterrupted() && !closing) {
			LoggerMessage message = take();
			if (message == null) continue;

			try {
				String text = message.text();
				if (text.isBlank() || text.equals("null")) continue;

				if (scheduler.parallel()) {
					scheduler.invokeAll(collectors.stream()
						.map(it -> callable(() -> it.collect(message)))
						.toList());
					continue;
				}

				collectors.forEach(it -> it.collect(message));
			} catch (InterruptedException exception) {
				throw new IllegalStateException("Thread interrupted while collecting", exception);
			}
		}
	}

	/// Retrieve and remove the head of the logging queue.
	/// This will block until a message is available.
	@Nullable
	private static LoggerMessage take() {
		try {
			LoggerMessage message = deque.take();
			if (!enabled(message.level())) return null;
			return message;
		} catch (InterruptedException exception) {
			return null;
		}
	}

	/// Shut down the logging system. This will wait for all collectors to
	/// consume their last logs. This is registered as a JVM shutdown hook.
	private static void close() {
		try {
			closing = true;
			while (!deque.isEmpty()) {
				onSpinWait();
				parkNanos(100L);
			}

			scheduler.close();
			collectors.forEach(LoggerCollector::close);
		} catch (Throwable throwable) {
			throw new IllegalStateException("Failed to shutdown logger", throwable);
		}
	}

	/// Dispatch (enqueue) the provided message.
	///
	/// @see LoggerMessage#builder() (creating a message)
	/// @see LoggerDispatcher#message() (convenience method)
	/// @see LoggerMethods (application logging)
	///
	/// @since 1.0
	public static void dispatch(LoggerMessage message) {
		try {
			if (closing) throw new IllegalStateException("Attempt to dispatch message at shutdown");
			deque.put(message);
		} catch (InterruptedException exception) {
			throw new IllegalStateException("Interrupted while dispatching message", exception);
		}
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

	//#region #format*
	/// Returns the provided string, formatted, or `null` if the provided string
	/// is `null`, or if the argument array is `null`.
	///
	/// @see LoggerFormatter
	/// @see #format(Locale, String, Object...)
	/// @since 1.0
	@Nullable
	@Contract("null, _ -> null; _, null -> param1; !null, !null -> new")
	public static String
	format(@Nullable String string, Object @Nullable ... arguments) {
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
	public static String
	format(@Nullable Locale locale, @Nullable String string, Object @Nullable ... arguments) {
		if (locale == null) return format(string, arguments);
		if (string == null) return null;
		if (arguments == null) return string;
		return formatter.formatString(locale, string, arguments);
	}
	//#endregion

	//#region #collector*
	/// Registers the provided collector to run when messages are being logged.
	///
	/// Generally, a collector should be an SPI provider ([LoggerExtension]).
	/// This can be used instead, for testing purposes or small applications.
	///
	/// @throws UnsupportedOperationException collector already registered
	/// @see #collectors
	/// @since 1.0
	public static void collector(LoggerCollector collector) {
		boolean added = collectors.add(collector);
		if (!added) throw new UnsupportedOperationException("Cannot register the same collector twice");
	}

	/// Registers the provided collectors to run when messages are being logged.
	///
	/// This is a delegate of convenience, for [#collector(LoggerCollector)],
	/// and invokes that method for each collector in the provided [Iterable].
	///
	/// This method is intended to be used for auto-wiring of collectors through
	/// dependency injection. A `List`/`Set` of implementations can be injected
	/// and provided to this method.
	///
	/// @see #collector
	/// @since 1.0
	public static void collectors(Iterable<LoggerCollector> collectors) {
		for (LoggerCollector collector : collectors) collector(collector);
	}

	/// Remove/unregister the provided collector, which must match the collector
	/// as either provided to [#collector], or created internally by the
	/// discovery of the collector as an SPI ([LoggerExtension]).
	///
	/// The check is performed using [Object#equals], so any collector can be
	/// made removable by making a deterministic implementation of [#equals].
	///
	/// This method should usually not be used.
	/// @since 1.0
	public static void collectorRemove(LoggerCollector collector) {
		collectors.remove(collector);
	}
	//#endregion
}