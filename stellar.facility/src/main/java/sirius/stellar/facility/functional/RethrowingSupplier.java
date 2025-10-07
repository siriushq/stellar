package sirius.stellar.facility.functional;

import org.jetbrains.annotations.Contract;

import java.util.function.Supplier;

import static sirius.stellar.facility.Strings.*;

/// Represents a supplier of results.
/// There is no requirement that a new or distinct result be returned each
/// time the supplier is invoked.
///
/// This supplier may throw a [Throwable] which is automatically caught
/// and rethrown, providing a temporary [Thread.UncaughtExceptionHandler] for
/// if the rethrown exception is unhandled, preventing any traceability issues.
///
/// This is a {@linkplain java.util.function functional interface} whose
/// functional method is [#getRethrowing()].
///
/// @see java.util.function.Supplier
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
@FunctionalInterface
public interface RethrowingSupplier<T> extends Supplier<T> {

	/// Gets a result.
	/// Implement this, rather than [#get()].
	T getRethrowing() throws Throwable;

	@Override
	default T get() {
		Thread thread = Thread.currentThread();
		Thread.UncaughtExceptionHandler handler = thread.getUncaughtExceptionHandler();

		try {
			return this.getRethrowing();
		} catch (Throwable throwable) {
			thread.setUncaughtExceptionHandler((exceptionThread, exception) -> {
				String name = exceptionThread.getName();
				System.err.println(format("Unhandled exception thrown from a RethrowingSupplier, executed on thread '{0}': {1}", name, exception));
			});
			throw new RuntimeException(throwable);
		} finally {
			thread.setUncaughtExceptionHandler(handler);
		}
	}

	/// Provides a [Supplier] for the provided [RethrowingSupplier].
	///
	/// This method performs no operation except allow the use of lambda syntax to
	/// construct a [RethrowingSupplier] for any method that normally accepts a
	/// [Supplier].
	@Contract("_ -> param1")
	static <T> Supplier<T> rethrowing(RethrowingSupplier<T> supplier) {
		return supplier;
	}
}