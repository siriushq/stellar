package sirius.stellar.facility;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/// Provides a facility for managing and closing multiple [AutoCloseable] resources.
///
/// This class can be used as a lightweight alternative to nested `try-with-resources` blocks.
/// Resources are registered in a stack-like structure (LIFO order) via [#manage(AutoCloseable)],
/// and are closed in reverse order when [#close()] is invoked.
///
/// Conceptually, this system works similarly to _arena allocation_ strategies where resources
/// are grouped into a managed arena, and it is collectively torn down at the end of lifetime.
///
/// If any resource throws an exception on closing, the first exception is rethrown (and wrapped as
/// a [RuntimeException], preventing unspecific catch blocks), and subsequent exceptions are added
/// to it as suppressed exceptions, preserving the full closure context.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public record Closer(Deque<AutoCloseable> stack) implements AutoCloseable {

	/// Creates a new [Closer] with an empty stack of resources.
	/// This method of instantiation does not use a thread-safe stack structure.
	///
	/// @since 1.0
	public static Closer create() {
		return new Closer(new ArrayDeque<>());
	}

	/// Creates a new [Closer] with an empty stack of resources.
	/// This method of instantiation enforces a thread-safe stack structure.
	///
	/// @since 1.0
	public static Closer createConcurrent() {
		return new Closer(new ConcurrentLinkedDeque<>());
	}

	/// Registers an [AutoCloseable] resource for managed closing.
	///
	/// The resource is pushed onto the internal stack, and will be closed when [#close()]
	/// is invoked, in reverse order of registration. This ensures predictable teardown order,
	/// mirroring the semantics of nested `try-with-resources`.
	///
	/// @param <T> Any object implementing [AutoCloseable].
	/// @return The provided object, allowing fluent usage.
	/// @since 1.0
	public <T extends AutoCloseable> T manage(T closeable) {
		this.stack.addFirst(closeable);
		return closeable;
	}

	@Override
	public void close() {
		RuntimeException exception = null;

		while (!this.stack.isEmpty() && !Thread.currentThread().isInterrupted()) {
			try {
				AutoCloseable current = this.stack.pollFirst();
				current.close();
			} catch (Throwable current) {
				if (exception != null) {
					exception.addSuppressed(current);
					continue;
				}
				exception = new RuntimeException(current);
			}
		}

		if (exception == null) return;
		throw exception;
	}
}