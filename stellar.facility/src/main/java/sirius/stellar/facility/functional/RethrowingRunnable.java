package sirius.stellar.facility.functional;

import org.jetbrains.annotations.Contract;

import static sirius.stellar.facility.Strings.*;

/// Represents an operation, that does not return a result, but may throw a
/// [Throwable] which is automatically caught and rethrown, providing a temporary
/// [Thread.UncaughtExceptionHandler] for if the rethrown exception is unhandled,
/// preventing any traceability issues.
///
/// This is a {@linkplain java.util.function functional interface} whose
/// functional method is [#runRethrowing()].
///
/// @see java.lang.Runnable
///
/// @since 1.0
/// @author Mechite
@FunctionalInterface
public interface RethrowingRunnable extends Runnable {

	/// Runs this operation.
	/// Implement this, rather than [#run()].
	void runRethrowing();

	@Override
	default void run() {
		Thread thread = Thread.currentThread();
		Thread.UncaughtExceptionHandler handler = thread.getUncaughtExceptionHandler();

		try {
			this.runRethrowing();
		} catch (Throwable throwable) {
			thread.setUncaughtExceptionHandler((exceptionThread, exception) -> {
				String name = exceptionThread.getName();
				System.err.println(format("Unhandled exception thrown from a RethrowingRunnable, executed on thread '{0}': {1}", name, exception));
			});
			throw new RuntimeException(throwable);
		} finally {
			thread.setUncaughtExceptionHandler(handler);
		}
	}

	/// Provides a [Runnable] for the provided [RethrowingRunnable].
	///
	/// This method performs no operation except allow the use of lambda syntax to
	/// construct a [RethrowingRunnable] for any method that normally accepts a
	/// [Runnable].
	@Contract("_ -> param1")
	static Runnable rethrowing(RethrowingRunnable runnable) {
		return runnable;
	}
}