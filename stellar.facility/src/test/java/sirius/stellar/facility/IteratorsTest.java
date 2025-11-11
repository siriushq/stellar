package sirius.stellar.facility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

final class IteratorsTest {

	@Test @DisplayName("from(T...) returns valid, resettable iterator starting from index 0")
	void fromReturnsValidResettableIteratorStartingFromIndex0() {
		var iterator = Iterators.from("first", "second", "third");
		assertThat(iterator).isInstanceOf(Iterators.Resettable.class);

		assertThatNoException().isThrownBy(() -> {
			var a = iterator.next();
			var b = iterator.next();
			var c = iterator.next();

			iterator.reset();
			var d = iterator.next();

			assertThat(a).isEqualTo("first");
			assertThat(b).isEqualTo("second");
			assertThat(c).isEqualTo("third");
			assertThat(d).isEqualTo("first");
		});
	}

	@Test @DisplayName("from(int, int, T...) returns valid, resettable iterator starting from and ending at provided index")
	void fromWithStartEndIndexesReturnsValidResettableIterator() {
		var values = new String[]{"first", "second", "third", "fourth"};
		var iterator = Iterators.from(1, 2, values);
		assertThat(iterator).isInstanceOf(Iterators.Resettable.class);

		var a = iterator.next();
		var b = iterator.next();

		assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(iterator::next);

		assertThat(a).isEqualTo("second");
		assertThat(b).isEqualTo("third");

		iterator.reset();
		assertThat(iterator.next()).isEqualTo("second");
	}

	@Test @DisplayName("from(T, UnaryOperator) returns valid, resettable iterator")
	void fromWithUnaryOperatorReturnsValidResettableIterator() {
		var random = new Random();
		var seed = "abcdefghijklmnopqrstuvwxyz";

		var iterator = Iterators.from(seed, next -> Strings.shuffle(random, next));
		assertThat(iterator).isInstanceOf(Iterators.Resettable.class);

		assertThatNoException().isThrownBy(() -> {
			var a = iterator.next();
			var b = iterator.next();

			assertThat(a).isEqualTo(seed);
			assertThat(b).isNotEqualTo(seed);

			iterator.reset();
			assertThat(iterator.next()).isEqualTo(seed);
		});
	}
}