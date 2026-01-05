package sirius.stellar.logging;

import org.jspecify.annotations.Nullable;
import sirius.stellar.logging.supplier.ObjectSupplier;
import sirius.stellar.logging.supplier.ThrowableSupplier;

import java.util.Arrays;
import java.util.function.Supplier;

import static java.lang.StackWalker.Option.*;
import static java.lang.Thread.*;
import static java.time.Instant.*;
import static sirius.stellar.logging.Logger.*;
import static sirius.stellar.logging.LoggerLevel.*;

/// This class encapsulates all statically-accessible logging functions from the
/// context of the public [Logger] class. They should be called against it,
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
///   respective level is disabled.
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
///   [#information(ThrowableSupplier)].
sealed abstract class LoggerMethods permits Logger {

	private static final StackWalker walker = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);

	//#region Logging [information*]
	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(String text) {
		if (!enabled(INFORMATION)) return;
		dispatch(now(), INFORMATION, currentThread().getName(), walker.getCallerClass().getName(), text);
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(Object object) {
		if (!enabled(INFORMATION)) return;
		dispatch(now(), INFORMATION, currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(object));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(String text, Object argument) {
		if (!enabled(INFORMATION)) return;
		dispatch(now(), INFORMATION, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(String text, Object argument1, Object argument2) {
		if (!enabled(INFORMATION)) return;
		dispatch(now(), INFORMATION, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(INFORMATION)) return;
		dispatch(now(), INFORMATION, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2, argument3));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(String text, Object... arguments) {
		if (!enabled(INFORMATION)) return;
		dispatch(now(), INFORMATION, currentThread().getName(), walker.getCallerClass().getName(), format(text, arguments));
	}
	//#endregion
	//#region Logging [information*, Lambda]
	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(@Nullable ObjectSupplier supplier) {
		if (!enabled(INFORMATION)) return;
		if (supplier == null) return;
		dispatch(now(), INFORMATION, currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(supplier.get()));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(String text, @Nullable ObjectSupplier argument) {
		if (!enabled(INFORMATION)) return;
		if (argument == null) return;
		dispatch(now(), INFORMATION, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument.get()));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2) {
		if (!enabled(INFORMATION)) return;
		if (argument1 == null || argument2 == null) return;
		dispatch(now(), INFORMATION, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get()));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2, @Nullable ObjectSupplier argument3) {
		if (!enabled(INFORMATION)) return;
		if (argument1 == null || argument2 == null || argument3 == null) return;
		dispatch(now(), INFORMATION, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get(), argument3.get()));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void information(String text, ObjectSupplier @Nullable... arguments) {
		if (!enabled(INFORMATION)) return;
		if (arguments == null) return;
		dispatch(now(), INFORMATION, currentThread().getName(), walker.getCallerClass().getName(), format(text, Arrays.stream(arguments).map(Supplier::get).toArray()));
	}
	//#endregion

	//#region Logging [warning*]
	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(String text) {
		if (!enabled(WARNING)) return;
		dispatch(now(), WARNING, currentThread().getName(), walker.getCallerClass().getName(), text);
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(Object object) {
		if (!enabled(WARNING)) return;
		dispatch(now(), WARNING, currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(object));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(String text, Object argument) {
		if (!enabled(WARNING)) return;
		dispatch(now(), WARNING, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(String text, Object argument1, Object argument2) {
		if (!enabled(WARNING)) return;
		dispatch(now(), WARNING, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(WARNING)) return;
		dispatch(now(), WARNING, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2, argument3));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(String text, Object... arguments) {
		if (!enabled(WARNING)) return;
		dispatch(now(), WARNING, currentThread().getName(), walker.getCallerClass().getName(), format(text, arguments));
	}
	//#endregion
	//#region Logging [warning*, Lambda]
	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(@Nullable ObjectSupplier supplier) {
		if (!enabled(WARNING)) return;
		if (supplier == null) return;
		dispatch(now(), WARNING, currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(supplier.get()));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(String text, @Nullable ObjectSupplier argument) {
		if (!enabled(WARNING)) return;
		if (argument == null) return;
		dispatch(now(), WARNING, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument.get()));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2) {
		if (!enabled(WARNING)) return;
		if (argument1 == null || argument2 == null) return;
		dispatch(now(), WARNING, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get()));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2, @Nullable ObjectSupplier argument3) {
		if (!enabled(WARNING)) return;
		if (argument1 == null || argument2 == null || argument3 == null) return;
		dispatch(now(), WARNING, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get(), argument3.get()));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void warning(String text, ObjectSupplier @Nullable... arguments) {
		if (!enabled(WARNING)) return;
		if (arguments == null) return;
		dispatch(now(), WARNING, currentThread().getName(), walker.getCallerClass().getName(), format(text, Arrays.stream(arguments).map(Supplier::get).toArray()));
	}
	//#endregion

	//#region Logging [error*]
	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void error(String text) {
		if (!enabled(LoggerLevel.ERROR)) return;
		dispatch(now(), LoggerLevel.ERROR, currentThread().getName(), walker.getCallerClass().getName(), text);
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void error(Object object) {
		if (!enabled(LoggerLevel.ERROR)) return;
		dispatch(now(), LoggerLevel.ERROR, currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(object));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void error(String text, Object argument) {
		if (!enabled(LoggerLevel.ERROR)) return;
		dispatch(now(), LoggerLevel.ERROR, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void error(String text, Object argument1, Object argument2) {
		if (!enabled(LoggerLevel.ERROR)) return;
		dispatch(now(), LoggerLevel.ERROR, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void error(String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(LoggerLevel.ERROR)) return;
		dispatch(now(), LoggerLevel.ERROR, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2, argument3));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void error(String text, Object... arguments) {
		if (!enabled(LoggerLevel.ERROR)) return;
		dispatch(now(), LoggerLevel.ERROR, currentThread().getName(), walker.getCallerClass().getName(), format(text, arguments));
	}
	//#endregion
	//#region Logging [error*, Lambda]
	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void error(@Nullable ObjectSupplier supplier) {
		if (!enabled(LoggerLevel.ERROR)) return;
		if (supplier == null) return;
		dispatch(now(), LoggerLevel.ERROR, currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(supplier.get()));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void error(String text, @Nullable ObjectSupplier argument) {
		if (!enabled(LoggerLevel.ERROR)) return;
		if (argument == null) return;
		dispatch(now(), LoggerLevel.ERROR, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument.get()));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void error(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2) {
		if (!enabled(LoggerLevel.ERROR)) return;
		if (argument1 == null || argument2 == null) return;
		dispatch(now(), LoggerLevel.ERROR, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get()));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void error(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2, @Nullable ObjectSupplier argument3) {
		if (!enabled(LoggerLevel.ERROR)) return;
		if (argument1 == null || argument2 == null || argument3 == null) return;
		dispatch(now(), LoggerLevel.ERROR, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get(), argument3.get()));
	}

	/// @see LoggerMethods (details of this method)
	/// @since 1.0
	public static void error(String text, ObjectSupplier @Nullable... arguments) {
		if (!enabled(LoggerLevel.ERROR)) return;
		if (arguments == null) return;
		dispatch(now(), LoggerLevel.ERROR, currentThread().getName(), walker.getCallerClass().getName(), format(text, Arrays.stream(arguments).map(Supplier::get).toArray()));
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
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), text);
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This uses {@link String#valueOf(Object)} only if the logger is enabled at this level.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(Object object) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(object));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts a single object as an argument for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(String text, Object argument) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts two objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(String text, Object argument1, Object argument2) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts three objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2, argument3));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(String text, Object... arguments) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), format(text, arguments));
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
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), traceback(throwable));
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
		text += "\n" + traceback(throwable);
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), text);
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
		String text = object + "\n" + traceback(throwable);
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), text);
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts a single object as an argument for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(Throwable throwable, String text, Object argument) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + traceback(throwable);
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts two objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(Throwable throwable, String text, Object argument1, Object argument2) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + traceback(throwable);
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts three objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(Throwable throwable, String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + traceback(throwable);
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2, argument3));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(Throwable throwable, String text, Object... arguments) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + traceback(throwable);
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), format(text, arguments));
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
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(supplier.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts a single object supplier as an argument for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(String text, @Nullable ObjectSupplier argument) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		if (argument == null) return;
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts two object suppliers as arguments for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		if (argument1 == null || argument2 == null) return;
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts three object suppliers as arguments for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2, @Nullable ObjectSupplier argument3) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		if (argument1 == null || argument2 == null || argument3 == null) return;
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get(), argument3.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(String text, ObjectSupplier @Nullable... arguments) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		if (arguments == null) return;
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), format(text, Arrays.stream(arguments).map(Supplier::get).toArray()));
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
	 * the throwable is a heavy operation, e.g., a call to {@link Logger#format(String, Object...)} for building the
	 * message, supplying arguments that are heavy to evaluate. This is why the lambda pattern is ideal for this type
	 * of scenario when making debug records in the log.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(ThrowableSupplier throwable) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), traceback(throwable.get()));
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
	 * the throwable is a heavy operation, e.g., a call to {@link Logger#format(String, Object...)} for building the
	 * message, supplying arguments that are heavy to evaluate. This is why the lambda pattern is ideal for this type
	 * of scenario when making debug records in the log.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(ThrowableSupplier throwable, String text) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + traceback(throwable.get());
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), text);
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
	 * the throwable is a heavy operation, e.g., a call to {@link Logger#format(String, Object...)} for building the
	 * message, supplying arguments that are heavy to evaluate. This is why the lambda pattern is ideal for this type
	 * of scenario when making debug records in the log.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(ThrowableSupplier throwable, Object object) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		String text = object + "\n" + traceback(throwable.get());
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), text);
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts a single object as an argument for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 * <p>
	 * A supplier is used on this method to allow for the throwable not to be evaluated (and stored), useful for if
	 * obtaining an instance of the throwable is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Obtaining an instance of a throwable can be a heavy operation if evaluating the arguments to the constructor of
	 * the throwable is a heavy operation, e.g., a call to {@link Logger#format(String, Object...)} for building the
	 * message, supplying arguments that are heavy to evaluate. This is why the lambda pattern is ideal for this type
	 * of scenario when making debug records in the log.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(ThrowableSupplier throwable, String text, Object argument) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + traceback(throwable.get());
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts two objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 * <p>
	 * A supplier is used on this method to allow for the throwable not to be evaluated (and stored), useful for if
	 * obtaining an instance of the throwable is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Obtaining an instance of a throwable can be a heavy operation if evaluating the arguments to the constructor of
	 * the throwable is a heavy operation, e.g., a call to {@link Logger#format(String, Object...)} for building the
	 * message, supplying arguments that are heavy to evaluate. This is why the lambda pattern is ideal for this type
	 * of scenario when making debug records in the log.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(ThrowableSupplier throwable, String text, Object argument1, Object argument2) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + traceback(throwable.get());
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * This accepts three objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 * <p>
	 * A supplier is used on this method to allow for the throwable not to be evaluated (and stored), useful for if
	 * obtaining an instance of the throwable is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Obtaining an instance of a throwable can be a heavy operation if evaluating the arguments to the constructor of
	 * the throwable is a heavy operation, e.g., a call to {@link Logger#format(String, Object...)} for building the
	 * message, supplying arguments that are heavy to evaluate. This is why the lambda pattern is ideal for this type
	 * of scenario when making debug records in the log.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(ThrowableSupplier throwable, String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + traceback(throwable.get());
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2, argument3));
	}

	/**
	 * Logs a message at {@link LoggerLevel#STACKTRACE}.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 * The stacktrace for the provided {@link Throwable} is printed out too.
	 * <p>
	 * A supplier is used on this method to allow for the throwable not to be evaluated (and stored), useful for if
	 * obtaining an instance of the throwable is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Obtaining an instance of a throwable can be a heavy operation if evaluating the arguments to the constructor of
	 * the throwable is a heavy operation, e.g., a call to {@link Logger#format(String, Object...)} for building the
	 * message, supplying arguments that are heavy to evaluate. This is why the lambda pattern is ideal for this type
	 * of scenario when making debug records in the log.
	 *
	 * @since 1.0
	 */
	public static void stacktrace(ThrowableSupplier throwable, String text, Object... arguments) {
		if (!enabled(LoggerLevel.STACKTRACE)) return;
		text += "\n" + traceback(throwable.get());
		dispatch(now(), LoggerLevel.STACKTRACE, currentThread().getName(), walker.getCallerClass().getName(), format(text, arguments));
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
		dispatch(now(), LoggerLevel.DEBUGGING, currentThread().getName(), walker.getCallerClass().getName(), text);
	}

	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * This uses {@link String#valueOf(Object)} only if the logger is enabled at this level.
	 *
	 * @since 1.0
	 */
	public static void debugging(Object object) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		dispatch(now(), LoggerLevel.DEBUGGING, currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(object));
	}

	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * This accepts a single object as an argument for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void debugging(String text, Object argument) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		dispatch(now(), LoggerLevel.DEBUGGING, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument));
	}

	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * This accepts two objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void debugging(String text, Object argument1, Object argument2) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		dispatch(now(), LoggerLevel.DEBUGGING, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2));
	}

	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * This accepts three objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void debugging(String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		dispatch(now(), LoggerLevel.DEBUGGING, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2, argument3));
	}

	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void debugging(String text, Object... arguments) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		dispatch(now(), LoggerLevel.DEBUGGING, currentThread().getName(), walker.getCallerClass().getName(), format(text, arguments));
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
		dispatch(now(), LoggerLevel.DEBUGGING, currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(supplier.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * This accepts a single object supplier as an argument for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void debugging(String text, @Nullable ObjectSupplier argument) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		if (argument == null) return;
		dispatch(now(), LoggerLevel.DEBUGGING, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * This accepts two object suppliers as arguments for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void debugging(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		if (argument1 == null || argument2 == null) return;
		dispatch(now(), LoggerLevel.DEBUGGING, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * This accepts three object suppliers as arguments for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void debugging(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2, @Nullable ObjectSupplier argument3) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		if (argument1 == null || argument2 == null || argument3 == null) return;
		dispatch(now(), LoggerLevel.DEBUGGING, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get(), argument3.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#DEBUGGING}.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void debugging(String text, ObjectSupplier @Nullable... arguments) {
		if (!enabled(LoggerLevel.DEBUGGING)) return;
		if (arguments == null) return;
		dispatch(now(), LoggerLevel.DEBUGGING, currentThread().getName(), walker.getCallerClass().getName(), format(text, Arrays.stream(arguments).map(Supplier::get).toArray()));
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
		dispatch(now(), LoggerLevel.CONFIGURATION, currentThread().getName(), walker.getCallerClass().getName(), text);
	}

	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * This uses {@link String#valueOf(Object)} only if the logger is enabled at this level.
	 *
	 * @since 1.0
	 */
	public static void configuration(Object object) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		dispatch(now(), LoggerLevel.CONFIGURATION, currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(object));
	}

	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * This accepts a single object as an argument for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void configuration(String text, Object argument) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		dispatch(now(), LoggerLevel.CONFIGURATION, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument));
	}

	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * This accepts two objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void configuration(String text, Object argument1, Object argument2) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		dispatch(now(), LoggerLevel.CONFIGURATION, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2));
	}

	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * This accepts three objects as arguments for formatting to prevent the creation of an array.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void configuration(String text, Object argument1, Object argument2, Object argument3) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		dispatch(now(), LoggerLevel.CONFIGURATION, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1, argument2, argument3));
	}

	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void configuration(String text, Object... arguments) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		dispatch(now(), LoggerLevel.CONFIGURATION, currentThread().getName(), walker.getCallerClass().getName(), format(text, arguments));
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
		dispatch(now(), LoggerLevel.CONFIGURATION, currentThread().getName(), walker.getCallerClass().getName(), String.valueOf(supplier.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * This accepts a single object supplier as an argument for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void configuration(String text, @Nullable ObjectSupplier argument) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		if (argument == null) return;
		dispatch(now(), LoggerLevel.CONFIGURATION, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * This accepts two object suppliers as arguments for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void configuration(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		if (argument1 == null || argument2 == null) return;
		dispatch(now(), LoggerLevel.CONFIGURATION, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * This accepts three object suppliers as arguments for formatting to prevent the creation of an array.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void configuration(String text, @Nullable ObjectSupplier argument1, @Nullable ObjectSupplier argument2, @Nullable ObjectSupplier argument3) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		if (argument1 == null || argument2 == null || argument3 == null) return;
		dispatch(now(), LoggerLevel.CONFIGURATION, currentThread().getName(), walker.getCallerClass().getName(), format(text, argument1.get(), argument2.get(), argument3.get()));
	}

	/**
	 * Logs a message at {@link LoggerLevel#CONFIGURATION}.
	 * <p>
	 * A supplier is used on this method to allow for the object not to be evaluated (and stored), useful for if
	 * obtaining an instance of the object is a heavy operation that should only be performed if logging is enabled.
	 * <p>
	 * Formatting is performed with {@link Logger#format(String, Object...)}.
	 *
	 * @since 1.0
	 */
	public static void configuration(String text, ObjectSupplier @Nullable... arguments) {
		if (!enabled(LoggerLevel.CONFIGURATION)) return;
		if (arguments == null) return;
		dispatch(now(), LoggerLevel.CONFIGURATION, currentThread().getName(), walker.getCallerClass().getName(), format(text, Arrays.stream(arguments).map(Supplier::get).toArray()));
	}
	//#endregion
}