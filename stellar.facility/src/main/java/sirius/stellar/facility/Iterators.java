package sirius.stellar.facility;

import sirius.stellar.facility.doctation.Internal;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static sirius.stellar.facility.Strings.*;

/// Provides a facility for creating [Iterator]s.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public class Iterators {

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
	public static <T> Iterators.Resettable<T> from(T seed, UnaryOperator<T> next) {
		return new TraversalIterator<>(seed, next);
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
		if (this.array == null) return false;
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
	private final UnaryOperator<T> operator;

	TraversalIterator(T seed, UnaryOperator<T> operator) {
		this.previous = seed;
		this.traversing = false;

		this.first = seed;
		this.operator = operator;
	}

	@Override
	public boolean hasNext() {
		if (!this.traversing) return true;
		return operator.apply(this.previous) != null;
	}

	@Override
	public T next() {
		if (!hasNext()) throw new NoSuchElementException();
		if (!this.traversing) {
			this.traversing = true;
			return this.previous;
		}
		return operator.apply(this.previous);
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