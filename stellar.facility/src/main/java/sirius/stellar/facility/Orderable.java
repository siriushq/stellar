package sirius.stellar.facility;

/**
 * Extension of {@link Comparable} that increases the brevity of {@link Comparable#compareTo(Object)}.
 * When implementing this interface, {@link Comparable#compareTo(Object)} should not be overridden.
 * <p>
 * This is also a {@link FunctionalInterface} allowing for lambdas to create it, useful if a method
 * accepts it or {@link Comparable} (it is recommended that methods accept {@link Comparable} instead
 * of this interface, as it not only provides compatibility with more code but also is the correct
 * abstraction - this is simply a helper abstraction and the {@link Comparable#compareTo(Object)}
 * method provides all the information that a method would ever need).
 *
 * @since 1.0
 * @author Mechite
 */
@FunctionalInterface
public interface Orderable<T> extends Comparable<T> {

	/**
	 * Runs when {@link Comparable#compareTo(Object)} is run, allowing you
	 * to append comparisons to the builder which are eventually used to
	 * build a score for {@link Comparable#compareTo(Object)}.
	 */
	void compare(T other, Results results);

	@Override
	default int compareTo(T other) {
		Results results = new Results();
		this.compare(other, results);
		return results.result;
	}

	/**
	 * Builder class that assists in building the score for an {@link Orderable}.
	 */
	final class Results {

		private int result;

		private Results() {
			this.result = 0;
		}

		/**
		 * Appends the comparison of two {@code int}s.
		 */
		public Results append(int left, int right) {
			if (this.result == 0) this.result = Integer.compare(left, right);
			return this;
		}

		/**
		 * Appends the comparison of two {@code long}s.
		 */
		public Results append(long left, long right) {
			if (this.result == 0) this.result = Long.compare(left, right);
			return this;
		}

		/**
		 * Appends the comparison of two {@code float}s.
		 */
		public Results append(float left, float right) {
			if (this.result == 0) this.result = Float.compare(left, right);
			return this;
		}

		/**
		 * Appends the comparison of two {@code double}s.
		 */
		public Results append(double left, double right) {
			if (this.result == 0) this.result = Double.compare(left, right);
			return this;
		}

		/**
		 * Appends the comparison of two {@code boolean}s.
		 */
		public Results append(boolean left, boolean right) {
			if (this.result == 0) this.result = Boolean.compare(left, right);
			return this;
		}

		/**
		 * Appends the comparison of two {@code Object}s.
		 * If they are not instances of {@link Comparable}, no comparison is made.
		 */
		public Results append(Object left, Object right) {
			if (this.result != 0) return this;
			if (left instanceof Comparable<?> && right instanceof Comparable<?>) result = ((Comparable) left).compareTo(right);
			return this;
		}
	}
}