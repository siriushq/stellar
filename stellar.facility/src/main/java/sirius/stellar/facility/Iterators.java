package sirius.stellar.facility;

import org.jspecify.annotations.Nullable;
import sirius.stellar.facility.annotation.Internal;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

import static sirius.stellar.facility.Strings.*;

/// Provides a facility for creating and working with [Iterator]s.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class Iterators {

	/// Returns an iterator for the provided values.
	///
	/// This method should be preferred over using [Stream#of(Object[])] or [List#of(Object[])]
	/// and then running [Stream#iterator()] or [List#iterator()] to get an iterator for any given
	/// array, as it provides a minor performance advantage by not creating an intermediate object
	/// representation and providing an iterator implementation that is made specifically for arrays.
	///
	/// The iterator provided only supports [Iterator#hasNext()] and [Iterator#next()], as well
	/// as [Object#hashCode()] and [Object#toString()]. As it is an instance of [Resettable],
	/// it can be reset to the initial starting position with [Resettable#reset()].
	///
	/// When running [Iterator#next()], [NoSuchElementException] will be thrown if there are
	/// no more elements left to iterate over.
	///
	/// @see Iterators#from(int, int, Object[])
	/// @since 1.0
	@SafeVarargs
	public static <T> Iterators.Resettable<T> from(T... values) {
		return new ArrayIterator<>(0, values.length, values);
	}

	/// Returns an iterator for the provided values.
	///
	/// This method should be preferred over using [Stream#of(Object[])] or [List#of(Object[])]
	/// and then running [Stream#iterator()] or [List#iterator()] to get an iterator for any given
	/// array, as it provides a minor performance advantage by not creating an intermediate object
	/// representation and providing an iterator implementation that is made specifically for arrays.
	///
	/// The iterator provided only supports [Iterator#hasNext()] and [Iterator#next()], as well
	/// as [Object#hashCode()] and [Object#toString()]. As it is an instance of [Resettable],
	/// it can be reset to the initial starting position with [Resettable#reset()].
	///
	/// When running [Iterator#next()], [NoSuchElementException] will be thrown if there are
	/// no more elements left to iterate over.
	///
	/// @see Iterators#from(Object[])
	/// @since 1.0
	@SafeVarargs
	public static <T> Iterators.Resettable<T> from(int start, int end, T... values) {
		return new ArrayIterator<>(start, end, values);
	}

	/// Returns a traversal iterator starting with the provided seed.
	///
	/// The iterator provided only supports [Iterator#hasNext()] and [Iterator#next()], as well
	/// as [Object#hashCode()] and [Object#toString()]. As it is an instance of [Resettable],
	/// it can be reset to the initial starting position with [Resettable#reset()].
	///
	/// It will always begin from the seed element, then continue to execute the unary operator to obtain
	/// the next value when it is required. When [Iterator#hasNext()] is run, the stored previous value
	/// is not changed, but the unary operator is executed; if the return value is not null, it returns true.
	///
	/// This behavior means that the provided unary operator must be executable, even when there are no more
	/// values remaining — it should return null whenever the values have been exhausted. The seed element is
	/// always preserved, meaning it is possible to reset back to the beginning of the iterator.
	///
	/// @since 1.0
	public static <T> Iterators.Resettable<T> from(T seed, UnaryOperator<@Nullable T> next) {
		return new TraversalIterator<>(seed, next);
	}

	/// Returns an iterator wrapping the provided iterator associated to the provided [AutoCloseable]
	/// resource, closing it when [Iterator#hasNext()] returns `false` or [Iterator#next()] fails.
	///
	/// @since 1.0
	public static <T> Iterator<T> closing(Iterator<T> delegate, AutoCloseable resource) {
		return new ClosingIterator<>(delegate, resource);
	}

	/// Returns a primitive integer iterator wrapping (and auto-unboxing) the provided iterator.
	/// @since 1.0
	public static PrimitiveIterator.OfInt primitiveInt(Iterator<Integer> iterator) {
		return new PrimitiveWrapperIterator.OfInt(iterator);
	}

	/// Returns a primitive double iterator wrapping (and auto-unboxing) the provided iterator.
	/// @since 1.0
	public static PrimitiveIterator.OfDouble primitiveDouble(Iterator<Double> iterator) {
		return new PrimitiveWrapperIterator.OfDouble(iterator);
	}

	/// Returns a primitive long iterator wrapping (and auto-unboxing) the provided iterator.
	/// @since 1.0
	public static PrimitiveIterator.OfLong primitiveLong(Iterator<Long> iterator) {
		return new PrimitiveWrapperIterator.OfLong(iterator);
	}

	/// Represents any iterator that can be brought back to an initial state, allowing for reuse.
	/// This should be repeatable, i.e. [#reset()] should never throw an exception.
	///
	/// @author Mahied Maruf (mechite)
	/// @since 1.0
	public interface Resettable<T> extends Iterator<T> {

		/// Resets the iterator back to the starting position.
		void reset();
	}
}

/// An implementation of [Iterator] that is constructed from an array.
@Internal
final class ArrayIterator<T> implements Iterators.Resettable<T> {

	private final int start;
	private final int end;
	private final T[] array;

	private int index;

	ArrayIterator(int start, int end, T[] array) {
		this.start = start;
		this.end = end;
		this.array = array;

		this.index = start;
	}

	@Override
	public boolean hasNext() {
		return this.index <= this.end;
	}

	@Override
	public T next() {
		if (!hasNext()) throw new NoSuchElementException();
		return this.array[this.index++];
	}

	@Override
	public void reset() {
		this.index = this.start;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Arrays.hashCode(this.array), this.start, this.end);
	}

	@Override
	public String toString() {
		return format("ArrayIterator[array={0}, start={1}, end={2}]", this.array, this.start, this.end);
	}
}

/// An implementation of [Iterator] that obtains the next value dynamically.
@Internal
final class TraversalIterator<T> implements Iterators.Resettable<T> {

	private T previous;
	private boolean traversing;

	private final T first;
	private final UnaryOperator<@Nullable T> operator;

	TraversalIterator(T seed, UnaryOperator<@Nullable T> operator) {
		this.previous = seed;
		this.traversing = false;

		this.first = seed;
		this.operator = operator;
	}

	@Override
	public boolean hasNext() {
		if (!this.traversing) return true;
		return this.operator.apply(this.previous) != null;
	}

	@Override
	@Nullable
	public T next() {
		if (!hasNext()) throw new NoSuchElementException();
		if (!this.traversing) {
			this.traversing = true;
			return this.previous;
		}
		return this.operator.apply(this.previous);
	}

	@Override
	public void reset() {
		this.previous = this.first;
		this.traversing = false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.first, this.operator);
	}

	@Override
	public String toString() {
		return format("TraversalIterator[previous={0}, traversing={1}, first={2}, operator={3}]", this.previous, this.traversing, this.first, this.operator);
	}
}

/// An implementation of [Iterator] that can be associated with an [AutoCloseable]
/// resource, which is automatically closed by this implementation when the next
/// call to [#hasNext] returns `false`, or [#next] fails.
final class ClosingIterator<T> implements Iterator<T>, AutoCloseable {

	private final Iterator<T> delegate;
	private final AutoCloseable resource;

	private boolean closed;

	ClosingIterator(Iterator<T> delegate, AutoCloseable resource) {
		this.delegate = delegate;
		this.resource = resource;
		this.closed = false;
	}

	@Override
	public boolean hasNext() {
		if (this.closed) return false;
		boolean hasNext = this.delegate.hasNext();
		if (!hasNext) this.close();
		return hasNext;
	}

	@Override
	public T next() {
		try {
			if (this.closed) throw new NoSuchElementException();
			return this.delegate.next();
		} catch (NoSuchElementException exception) {
			this.close();
			throw exception;
		} finally {
			if (!this.delegate.hasNext()) this.close();
		}
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

/// Superclass for primitive iterators wrapping (and auto-unboxing) a provided boxed iterator.
abstract class PrimitiveWrapperIterator<T, T_CONS>
		implements PrimitiveIterator<T, T_CONS> {

	protected final Iterator<T> delegate;

	PrimitiveWrapperIterator(Iterator<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public boolean hasNext() {
		return this.delegate.hasNext();
	}

	/// A primitive integer iterator wrapping (and auto-unboxing) the provided iterator.
	static final class OfInt extends PrimitiveWrapperIterator<Integer, IntConsumer>
			implements PrimitiveIterator.OfInt {

		OfInt(Iterator<Integer> delegate) {
			super(delegate);
		}

		@Override
		public int nextInt() {
			return this.delegate.next();
		}
	}

	/// A primitive double iterator wrapping (and auto-unboxing) the provided iterator.
	static final class OfDouble extends PrimitiveWrapperIterator<Double, DoubleConsumer>
			implements PrimitiveIterator.OfDouble {

		OfDouble(Iterator<Double> delegate) {
			super(delegate);
		}

		@Override
		public double nextDouble() {
			return this.delegate.next();
		}
	}

	/// A primitive long iterator wrapping (and auto-unboxing) the provided iterator.
	static final class OfLong extends PrimitiveWrapperIterator<Long, LongConsumer>
			implements PrimitiveIterator.OfLong {

		OfLong(Iterator<Long> delegate) {
			super(delegate);
		}

		@Override
		public long nextLong() {
			return this.delegate.next();
		}
	}
}