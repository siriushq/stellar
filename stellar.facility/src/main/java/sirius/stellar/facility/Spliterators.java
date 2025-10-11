package sirius.stellar.facility;

import org.jspecify.annotations.Nullable;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/// Provides a facility for creating and working with [Spliterator]s.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class Spliterators {

	/// Returns a spliterator wrapping the provided spliterator associated to the provided [AutoCloseable]
	/// resource, closing it when [Spliterator#hasNext()] returns `false` or [Spliterator#next()] fails.
	///
	/// @since 1.0
	public static <T> Spliterator<T> closing(Spliterator<T> delegate, AutoCloseable resource) {
		return new ClosingSpliterator<>(delegate, resource);
	}

	/// Returns a primitive integer spliterator wrapping (and auto-unboxing) the provided spliterator.
	/// @since 1.0
	public static Spliterator.OfInt primitiveInt(Spliterator<Integer> spliterator) {
		return new Spliterator.OfInt() {

			@Override
			public OfInt trySplit() {
				return primitiveInt(spliterator.trySplit());
			}

			@Override
			public boolean tryAdvance(IntConsumer action) {
				return spliterator.tryAdvance(action::accept);
			}

			@Override
			public long estimateSize() {
				return spliterator.estimateSize();
			}

			@Override
			public int characteristics() {
				return spliterator.characteristics();
			}
		};
	}

	/// Returns a primitive double spliterator wrapping (and auto-unboxing) the provided spliterator.
	/// @since 1.0
	public static Spliterator.OfDouble primitiveDouble(Spliterator<Double> spliterator) {
		return new Spliterator.OfDouble() {

			@Override
			public OfDouble trySplit() {
				return primitiveDouble(spliterator.trySplit());
			}

			@Override
			public boolean tryAdvance(DoubleConsumer action) {
				return spliterator.tryAdvance(action::accept);
			}

			@Override
			public long estimateSize() {
				return spliterator.estimateSize();
			}

			@Override
			public int characteristics() {
				return spliterator.characteristics();
			}
		};
	}

	/// Returns a primitive long spliterator wrapping (and auto-unboxing) the provided spliterator.
	/// @since 1.0
	public static Spliterator.OfLong primitiveLong(Spliterator<Long> spliterator) {
		return new Spliterator.OfLong() {

			@Override
			public OfLong trySplit() {
				return primitiveLong(spliterator.trySplit());
			}

			@Override
			public boolean tryAdvance(LongConsumer action) {
				return spliterator.tryAdvance(action::accept);
			}

			@Override
			public long estimateSize() {
				return spliterator.estimateSize();
			}

			@Override
			public int characteristics() {
				return spliterator.characteristics();
			}
		};
	}
}

/// An implementation of [Spliterator] that can be associated with an [AutoCloseable]
/// resource, which is automatically closed by this implementation when the next
/// call to [#tryAdvance] returns `false`, or [#trySplit] fails.
final class ClosingSpliterator<T> implements Spliterator<T>, AutoCloseable {

	private final Spliterator<T> delegate;
	private final AutoCloseable resource;

	private boolean closed;

	ClosingSpliterator(Spliterator<T> delegate, AutoCloseable resource) {
		this.delegate = delegate;
		this.resource = resource;
		this.closed = false;
	}

	@Override
	public boolean tryAdvance(Consumer<? super T> action) {
		if (this.closed) return false;
		boolean tryAdvance = this.delegate.tryAdvance(action);
		if (!tryAdvance) this.close();
		return tryAdvance;
	}

	@Override @Nullable
	public Spliterator<T> trySplit() {
		if (this.closed) return null;

		Spliterator<T> result = this.delegate.trySplit();
		if (result == null) {
			this.close();
			return null;
		}

		return new ClosingSpliterator<>(result, this.resource);
	}

	@Override
	public long estimateSize() {
		return (this.closed) ? 0 : this.delegate.estimateSize();
	}

	@Override
	public int characteristics() {
		return this.delegate.characteristics();
	}

	@Override
	public void close() {
		try {
			if (this.closed) return;
			this.resource.close();
			this.closed = true;
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
}