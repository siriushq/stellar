package sirius.stellar.facility.functional;

import org.jetbrains.annotations.Contract;

import java.util.function.Consumer;

import static sirius.stellar.facility.Strings.*;

/**
 * Represents an operation that accepts a single input argument and returns no result,
 * but may throw a {@link Throwable} which is automatically caught and rethrown,
 * providing a temporary {@link Thread.UncaughtExceptionHandler} for if the rethrown
 * exception is unhandled, preventing any traceability issues.
 * <p>
 * This is a {@linkplain java.util.function functional interface} whose
 * functional method is {@link #acceptRethrowing(T)}.
 *
 * @see java.util.function.Consumer
 *
 * @since 1.0
 * @author Mechite
 */
@FunctionalInterface
public interface RethrowingConsumer<T> extends Consumer<T> {

	/**
     * Performs this operation on the provided argument.
	 * Implement this, rather than {@link Consumer#accept}.
     */
	void acceptRethrowing(T t) throws Throwable;

	@Override
	default void accept(T t) {
		Thread thread = Thread.currentThread();
		Thread.UncaughtExceptionHandler handler = thread.getUncaughtExceptionHandler();

		try {
			this.acceptRethrowing(t);
		} catch (Throwable throwable) {
			thread.setUncaughtExceptionHandler((exceptionThread, exception) -> {
				String name = exceptionThread.getName();
				System.err.println(format("Unhandled exception thrown from a RethrowingConsumer, executed on thread '{0}': {1}", name, exception));
			});
			throw new RuntimeException(throwable);
		} finally {
			thread.setUncaughtExceptionHandler(handler);
		}
	}

	/**
	 * Provides a {@link Consumer} for the provided {@link RethrowingConsumer}.
	 * <p>
	 * This method performs no operation except allow the use of lambda syntax to
	 * construct a {@link RethrowingConsumer} for any method that normally accepts a
	 * {@link Consumer}.
	 */
	@Contract("_ -> param1")
	static <T> Consumer<T> rethrowing(RethrowingConsumer<T> consumer) {
		return consumer;
	}
}