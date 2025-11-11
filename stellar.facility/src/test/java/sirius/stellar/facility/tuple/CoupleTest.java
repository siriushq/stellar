package sirius.stellar.facility.tuple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import sirius.stellar.facility.exception.ImmutableModificationException;

import static org.assertj.core.api.Assertions.*;
import static sirius.stellar.facility.tuple.Couple.*;

final class CoupleTest {

	@Test @DisplayName("immutableCouple returns immutable couple which throws ImmutableModificationException on modification")
	void immutableCoupleThrowsOnModification() {
		var couple = immutableCouple("a", "b");
		assertThatThrownBy(() -> couple.first("c")).isInstanceOf(ImmutableModificationException.class);
	}

	@Test @DisplayName("mutableCouple returns mutable couple, which doesn't throw ImmutableModificationException")
	void mutableCoupleAllowsModification() {
		var couple = mutableCouple("a", "b");
		couple.first("c");
		assertThat(couple.first()).isEqualTo("c");
		assertThatCode(() -> couple.first("x")).doesNotThrowAnyException();
	}

	@Test @DisplayName("factory methods return instances that correctly return values")
	void factoryMethodsReturnCorrectValues() {
		var immutableCouple = immutableCouple("a", "b");
		var mutableCouple = mutableCouple("c", "d");

		assertThat(immutableCouple.first()).isEqualTo("a");
		assertThat(immutableCouple.second()).isEqualTo("b");
		assertThat(mutableCouple.first()).isEqualTo("c");
		assertThat(mutableCouple.second()).isEqualTo("d");
	}

	@Test @DisplayName("instances correctly implement Map.Entry")
	void instancesImplementMapEntry() {
		var immutableCouple = immutableCouple("a", "b");
		var mutableCouple = mutableCouple("c", "d");

		assertThat(immutableCouple.getKey()).isEqualTo("a");
		assertThat(immutableCouple.getValue()).isEqualTo("b");
		assertThat(mutableCouple.getKey()).isEqualTo("c");
		assertThat(mutableCouple.getValue()).isEqualTo("d");
	}
}