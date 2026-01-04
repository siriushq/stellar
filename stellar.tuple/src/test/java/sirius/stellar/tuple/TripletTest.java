package sirius.stellar.tuple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static sirius.stellar.tuple.Triplet.immutableTriplet;
import static sirius.stellar.tuple.Triplet.mutableTriplet;

final class TripletTest {

	@Test @DisplayName("immutableTriplet throws UnsupportedOperationException on modification")
	void immutableTripletThrowsOnModification() {
		var triplet = immutableTriplet("a", "b", "c");
		assertThatThrownBy(() -> triplet.first("d")).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test @DisplayName("mutableTriplet allows modification")
	void mutableTripletAllowsModification() {
		var triplet = mutableTriplet("a", "b", "c");
		triplet.first("d");
		assertThat(triplet.first()).isEqualTo("d");
	}

	@Test @DisplayName("factory methods return correct values")
	void factoryMethodsReturnCorrectValues() {
		var immutableTriplet = immutableTriplet("a", "b", "c");
		var mutableTriplet = mutableTriplet("d", "e", "f");

		assertThat(immutableTriplet.first()).isEqualTo("a");
		assertThat(immutableTriplet.second()).isEqualTo("b");
		assertThat(immutableTriplet.third()).isEqualTo("c");

		assertThat(mutableTriplet.first()).isEqualTo("d");
		assertThat(mutableTriplet.second()).isEqualTo("e");
		assertThat(mutableTriplet.third()).isEqualTo("f");
	}
}