package sirius.stellar.logging;

import org.jspecify.annotations.Nullable;
import sirius.stellar.logging.supplier.ObjectSupplier;
import sirius.stellar.logging.supplier.ThrowableSupplier;

import java.time.Instant;
import java.util.Arrays;
import java.util.function.Supplier;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;
import static java.lang.Thread.currentThread;
import static java.time.Instant.now;
import static sirius.stellar.logging.Logger.*;
import static sirius.stellar.logging.LoggerLevel.INFORMATION;
import static sirius.stellar.logging.LoggerLevel.WARNING;

/// This class encapsulates all statically-accessible logging functions from
/// the context of the public [Logger] class. They should be called against it,
/// e.g. `Logger.information("Hello, world!")`.
///
/// While this class is marked `package-private` and `sealed`, the methods it
/// exposes are public API of [Logger] and can be used there.
///
/// - All methods will walk the stack in order to retrieve the caller class for
///   their respective invocations, using [StackWalker#getCallerClass()].
///
/// - Methods which accept no formatting arguments, are present to prevent the
///   creation of an array (when variadic argument methods can also be called
///   in the same fashion), e.g.,
///
///   [#information(String)].
///
/// - Methods which accept [Object] instead of [String] as the only argument,
///   such as [#information(Object)], will not call [#toString] if the logger is
///   not enabled at their corresponding level, e.g.,
///
///   [#information(Object)].
///
/// - Methods which accept both a formatting [String] and arguments array, will
///   perform their formatting using [Logger#format(String, Object...)], e.g.,
///
///   [#information(String, Object...)].
///
/// - Methods which accept a formatting [String] and multiple argument [Object]s
///   are provided to prevent the overhead of creating an array, when only up to
///   three arguments are provided, e.g.,
///
///   [#information(String, Object)],
///   [#information(String, Object, Object)],
///   [#information(String, Object, Object, Object)].
///
/// - Methods which accept an [ObjectSupplier] work akin to their non-supplier
///   variants, but allow for the object to never be evaluated if logging on the
///   respective level is disabled, e.g.
///
///   [#information(ObjectSupplier)] (the supplier usage will call [#toString]),
///   [#information(String, ObjectSupplier...)] (separately evaluate arguments),
///
///   (same as variadic method, just an optimization)
///   [#information(String, ObjectSupplier, ObjectSupplier)]
///   [#information(String, ObjectSupplier, ObjectSupplier, ObjectSupplier)]
///
///   e.g., formatting arguments of [#information(String, ObjectSupplier...)],
///   where they would individually be evaluated, or simply a non-format string
///   (or object to call [#toString] on) in [#information(ObjectSupplier)].
///
/// - Methods which accept a [Throwable] or [ThrowableSupplier] as their first
///   argument will print the stacktrace of the provided throwable, right after
///   the provided message, e.g.,
///
///   [#information(Throwable)],
///   [#information(Throwable, String, Object)],
///   [#information(Throwable, String, Object, Object)],
///   [#information(Throwable, String, Object, Object, Object)],
///   [#information(Throwable, String, Object...)],
///
///   [#information(ThrowableSupplier)],
///   [#information(ThrowableSupplier, ObjectSupplier)].
///
/// @see Logger
sealed abstract class LoggerMethods permits Logger {

	private static final StackWalker walker = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);

	//#region Logging [information*]
	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(String text) {
		if (!enabled(INFORMATION)) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(text)
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(Object object) {
		if (!enabled(INFORMATION)) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(String.valueOf(object))
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(String text, Object argument) {
		if (!enabled(INFORMATION)) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, argument))
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(String text, Object argument1, Object argument2) {
		if (!enabled(INFORMATION)) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, argument1, argument2))
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(INFORMATION)) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, argument1, argument2, argument3))
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(String text, Object... arguments) {
		if (!enabled(INFORMATION)) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, arguments))
				.dispatch();
	}
	//#endregion
	//#region Logging [information*, Lambda]
	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(@Nullable ObjectSupplier supplier) {
		if (!enabled(INFORMATION)) return;
		if (supplier == null) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(String.valueOf(supplier.get()))
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(String text, @Nullable ObjectSupplier argument) {
		if (!enabled(INFORMATION)) return;
		if (argument == null) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, argument.get()))
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2) {
		if (!enabled(INFORMATION)) return;
		if (argument1 == null || argument2 == null) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, argument1.get(), argument2.get()))
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2, @Nullable ObjectSupplier argument3) {
		if (!enabled(INFORMATION)) return;
		if (argument1 == null || argument2 == null || argument3 == null) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, argument1.get(), argument2.get(), argument3.get()))
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(String text, ObjectSupplier @Nullable... arguments) {
		if (!enabled(INFORMATION)) return;
		if (arguments == null) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, Arrays.stream(arguments)
					.map(Supplier::get)
					.toArray()))
				.dispatch();
	}
	//#endregion
	//#region Logging [information*, Throwable]
	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(Throwable throwable) {
		if (!enabled(INFORMATION)) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.throwable(throwable)
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(Throwable throwable, String text) {
		if (!enabled(INFORMATION)) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(text)
				.throwable(throwable)
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(Throwable throwable, String text, Object argument) {
		if (!enabled(INFORMATION)) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, argument))
				.throwable(throwable)
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(Throwable throwable, String text, Object argument1, Object argument2) {
		if (!enabled(INFORMATION)) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, argument1, argument2))
				.throwable(throwable)
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(Throwable throwable, String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(INFORMATION)) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, argument1, argument2, argument3))
				.throwable(throwable)
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(Throwable throwable, String text, Object... arguments) {
		if (!enabled(INFORMATION)) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, arguments))
				.throwable(throwable)
				.dispatch();
	}
	//#endregion
	//#region Logging [information*, ThrowableSupplier]
	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(ThrowableSupplier supplier) {
		if (!enabled(INFORMATION)) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.throwable(supplier.get())
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(ThrowableSupplier supplier, ObjectSupplier message) {
		if (!enabled(INFORMATION)) return;
		LoggerMessage.builder()
				.level(INFORMATION)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(String.valueOf(message.get()))
				.throwable(supplier.get())
				.dispatch();
	}
	//#endregion

	//#region Logging [warning*]
	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(String text) {
		if (!enabled(WARNING)) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(text)
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(Object object) {
		if (!enabled(WARNING)) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(String.valueOf(object))
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(String text, Object argument) {
		if (!enabled(WARNING)) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, argument))
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(String text, Object argument1, Object argument2) {
		if (!enabled(WARNING)) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, argument1, argument2))
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(WARNING)) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, argument1, argument2, argument3))
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(String text, Object... arguments) {
		if (!enabled(WARNING)) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, arguments))
				.dispatch();
	}
	//#endregion
	//#region Logging [warning*, Lambda]
	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(@Nullable ObjectSupplier supplier) {
		if (!enabled(WARNING)) return;
		if (supplier == null) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(String.valueOf(supplier.get()))
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(String text, @Nullable ObjectSupplier argument) {
		if (!enabled(WARNING)) return;
		if (argument == null) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, argument.get()))
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2) {
		if (!enabled(WARNING)) return;
		if (argument1 == null || argument2 == null) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, argument1.get(), argument2.get()))
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2, @Nullable ObjectSupplier argument3) {
		if (!enabled(WARNING)) return;
		if (argument1 == null || argument2 == null || argument3 == null) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, argument1.get(), argument2.get(), argument3.get()))
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(String text, ObjectSupplier @Nullable... arguments) {
		if (!enabled(WARNING)) return;
		if (arguments == null) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, Arrays.stream(arguments)
					.map(Supplier::get)
					.toArray()))
				.dispatch();
	}
	//#endregion
	//#region Logging [warning*, Throwable]
	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(Throwable throwable) {
		if (!enabled(WARNING)) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.throwable(throwable)
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(Throwable throwable, String text) {
		if (!enabled(WARNING)) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(text)
				.throwable(throwable)
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(Throwable throwable, String text, Object argument) {
		if (!enabled(WARNING)) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, argument))
				.throwable(throwable)
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(Throwable throwable, String text, Object argument1, Object argument2) {
		if (!enabled(WARNING)) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, argument1, argument2))
				.throwable(throwable)
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(Throwable throwable, String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(WARNING)) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, argument1, argument2, argument3))
				.throwable(throwable)
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(Throwable throwable, String text, Object... arguments) {
		if (!enabled(WARNING)) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(format(text, arguments))
				.throwable(throwable)
				.dispatch();
	}
	//#endregion
	//#region Logging [warning*, ThrowableSupplier]
	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(ThrowableSupplier supplier) {
		if (!enabled(WARNING)) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.throwable(supplier.get())
				.dispatch();
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(ThrowableSupplier supplier, ObjectSupplier message) {
		if (!enabled(WARNING)) return;
		LoggerMessage.builder()
				.level(WARNING)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(walker.getCallerClass().getName())
				.text(String.valueOf(message.get()))
				.throwable(supplier.get())
				.dispatch();
	}
	//#endregion
}