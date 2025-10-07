package sirius.stellar.facility;

/// Extension of [Comparable] that increases the brevity of [Comparable#compareTo(Object)].
/// When implementing this interface, [Comparable#compareTo(Object)] should not be overridden.
///
/// This is also a [FunctionalInterface] allowing for lambdas to create it, useful if a method
/// accepts it or [Comparable] (it is recommended that methods accept [Comparable] instead
/// of this interface, as it not only provides compatibility with more code but also is the correct
/// abstraction - this is simply a helper abstraction and the [Comparable#compareTo(Object)]
/// method provides all the information that a method would ever need).
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
@FunctionalInterface
public interface Orderable<T> extends Comparable<T> {

	/// Runs when [Comparable#compareTo(Object)] is run, allowing you
	/// to append comparisons to the builder which are eventually used to
	/// build a score for [Comparable#compareTo(Object)].
	void compare(T other, Results results);

	@Override
	default int compareTo(T other) {
		Results results = new Results();
		this.compare(other, results);
		return results.result;
	}

	/// Builder class that assists in building the score for an [Orderable].
	final class Results {

		private int result;

		private Results() {
			this.result = 0;
		}

		/// Appends the comparison of two `int`s.
		public Results append(int left, int right) {
			if (this.result == 0) this.result = Integer.compare(left, right);
			return this;
		}

		/// Appends the comparison of two `long`s.
		public Results append(long left, long right) {
			if (this.result == 0) this.result = Long.compare(left, right);
			return this;
		}

		/// Appends the comparison of two `float`s.
		public Results append(float left, float right) {
			if (this.result == 0) this.result = Float.compare(left, right);
			return this;
		}

		/// Appends the comparison of two `double`s.
		public Results append(double left, double right) {
			if (this.result == 0) this.result = Double.compare(left, right);
			return this;
		}

		/// Appends the comparison of two `boolean`s.
		public Results append(boolean left, boolean right) {
			if (this.result == 0) this.result = Boolean.compare(left, right);
			return this;
		}

		/// Appends the comparison of two `Object`s.
		/// If they are not instances of [Comparable], no comparison is made.
		public Results append(Object left, Object right) {
			if (this.result != 0) return this;
			if (left instanceof Comparable<?> && right instanceof Comparable<?>) result = ((Comparable) left).compareTo(right);
			return this;
		}
	}
}