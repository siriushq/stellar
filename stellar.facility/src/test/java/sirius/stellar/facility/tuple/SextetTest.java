package sirius.stellar.facility.tuple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import sirius.stellar.facility.exception.ImmutableModificationException;

import static org.assertj.core.api.Assertions.*;
import static sirius.stellar.facility.tuple.Sextet.*;

final class SextetTest {

	@Test @DisplayName("immutableSextet throws ImmutableModificationException on modification")
	void immutableSextetThrowsOnModification() {
		var sextet = immutableSextet("a", "b", "c", "d", "e", "f");
		assertThatThrownBy(() -> sextet.first("g")).isInstanceOf(ImmutableModificationException.class);
	}

	@Test @DisplayName("mutableSextet allows modification")
	void mutableSextetAllowsModification() {
		var sextet = mutableSextet("a", "b", "c", "d", "e", "f");
		sextet.first("g");
		assertThat(sextet.first()).isEqualTo("g");
	}

	@Test @DisplayName("factory methods return correct values")
	void factoryMethodsReturnCorrectValues() {
		var immutable = immutableSextet("a", "b", "c", "d", "e", "f");
		var mutable = mutableSextet("g", "h", "i", "j", "k", "l");

		assertThat(immutable.first()).isEqualTo("a");
		assertThat(immutable.second()).isEqualTo("b");
		assertThat(immutable.third()).isEqualTo("c");
		assertThat(immutable.fourth()).isEqualTo("d");
		assertThat(immutable.fifth()).isEqualTo("e");
		assertThat(immutable.sixth()).isEqualTo("f");

		assertThat(mutable.first()).isEqualTo("g");
		assertThat(mutable.second()).isEqualTo("h");
		assertThat(mutable.third()).isEqualTo("i");
		assertThat(mutable.fourth()).isEqualTo("j");
		assertThat(mutable.fifth()).isEqualTo("k");
		assertThat(mutable.sixth()).isEqualTo("l");
	}
}