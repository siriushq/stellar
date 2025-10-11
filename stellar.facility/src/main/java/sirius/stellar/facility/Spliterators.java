package sirius.stellar.facility;

import org.jspecify.annotations.Nullable;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

import static sirius.stellar.facility.Spliterators.*;

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
		return new PrimitiveWrapperSpliterator.OfInt(spliterator);
	}

	/// Returns a primitive double spliterator wrapping (and auto-unboxing) the provided spliterator.
	/// @since 1.0
	public static Spliterator.OfDouble primitiveDouble(Spliterator<Double> spliterator) {
		return new PrimitiveWrapperSpliterator.OfDouble(spliterator);
	}

	/// Returns a primitive long spliterator wrapping (and auto-unboxing) the provided spliterator.
	/// @since 1.0
	public static Spliterator.OfLong primitiveLong(Spliterator<Long> spliterator) {
		return new PrimitiveWrapperSpliterator.OfLong(spliterator);
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

/// Superclass for primitive integer spliterators wrapping (and auto-unboxing) a provided boxed spliterator.
abstract class PrimitiveWrapperSpliterator<T, T_CONS, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>>
		implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {

	protected final Spliterator<T> delegate;

	PrimitiveWrapperSpliterator(Spliterator<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public long estimateSize() {
		return this.delegate.estimateSize();
	}

	@Override
	public int characteristics() {
		return this.delegate.characteristics();
	}

	/// A primitive integer spliterator wrapping (and auto-unboxing) the provided spliterator.
	static final class OfInt
			extends PrimitiveWrapperSpliterator<Integer, IntConsumer, Spliterator.OfInt>
			implements Spliterator.OfInt {

		OfInt(Spliterator<Integer> delegate) {
			super(delegate);
		}

		@Override
		public Spliterator.OfInt trySplit() {
			return primitiveInt(super.delegate.trySplit());
		}

		@Override
		public boolean tryAdvance(IntConsumer action) {
			return this.delegate.tryAdvance(action::accept);
		}
	}

	/// A primitive double spliterator wrapping (and auto-unboxing) the provided spliterator.
	static final class OfDouble
			extends PrimitiveWrapperSpliterator<Double, DoubleConsumer, Spliterator.OfDouble>
			implements Spliterator.OfDouble {

		OfDouble(Spliterator<Double> delegate) {
			super(delegate);
		}

		@Override
		public Spliterator.OfDouble trySplit() {
			return primitiveDouble(super.delegate.trySplit());
		}

		@Override
		public boolean tryAdvance(DoubleConsumer action) {
			return this.delegate.tryAdvance(action::accept);
		}
	}

	/// A primitive long spliterator wrapping (and auto-unboxing) the provided spliterator.
	static final class OfLong
			extends PrimitiveWrapperSpliterator<Long, LongConsumer, Spliterator.OfLong>
			implements Spliterator.OfLong {

		OfLong(Spliterator<Long> delegate) {
			super(delegate);
		}

		@Override
		public Spliterator.OfLong trySplit() {
			return primitiveLong(super.delegate.trySplit());
		}

		@Override
		public boolean tryAdvance(LongConsumer action) {
			return this.delegate.tryAdvance(action::accept);
		}
	}
}