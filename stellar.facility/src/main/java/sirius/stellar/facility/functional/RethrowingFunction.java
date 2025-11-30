package sirius.stellar.facility.functional;

import sirius.stellar.annotation.Contract;

import java.util.function.Function;

import static sirius.stellar.facility.Strings.*;

/// Represents a function that accepts one argument and produces a result,
/// but may throw a [Throwable] which is automatically caught and rethrown,
/// providing a temporary [Thread.UncaughtExceptionHandler] for if the rethrown
/// exception is unhandled, preventing any traceability issues.
///
/// This is a {@linkplain java.util.function functional interface} whose
/// functional method is [#applyRethrowing(T)].
///
/// @see java.util.function.Function
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
@FunctionalInterface
public interface RethrowingFunction<T, R> extends Function<T, R> {

	/// Applies this function to the provided argument.
	/// Implement this, rather than [#apply].
	R applyRethrowing(T t) throws Throwable;

	@Override
	default R apply(T t) {
		Thread thread = Thread.currentThread();
		Thread.UncaughtExceptionHandler handler = thread.getUncaughtExceptionHandler();

		try {
			return this.applyRethrowing(t);
		} catch (Throwable throwable) {
			thread.setUncaughtExceptionHandler((exceptionThread, exception) -> {
				String name = exceptionThread.getName();
				System.err.println(format("Unhandled exception thrown from a RethrowingFunction, executed on thread '{0}': {1}", name, exception));
			});
			throw new RuntimeException(throwable);
		} finally {
			thread.setUncaughtExceptionHandler(handler);
		}
	}

	/// Provides a [Function] for the provided [RethrowingFunction].
	///
	/// This method performs no operation except allow the use of lambda syntax to
	/// construct a [RethrowingFunction] for any method that normally accepts a
	/// [Function].
	@Contract("_ -> param1")
	static <T, R> Function<T, R> rethrowing(RethrowingFunction<T, R> function) {
		return function;
	}
}