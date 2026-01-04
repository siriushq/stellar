package sirius.stellar.tuple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static sirius.stellar.tuple.Septet.immutableSeptet;
import static sirius.stellar.tuple.Septet.mutableSeptet;

final class SeptetTest {

	@Test @DisplayName("immutableSeptet throws UnsupportedOperationException on modification")
	void immutableSeptetThrowsOnModification() {
		var septet = immutableSeptet("a", "b", "c", "d", "e", "f", "g");
		assertThatThrownBy(() -> septet.first("h")).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test @DisplayName("mutableSeptet allows modification")
	void mutableSeptetAllowsModification() {
		var septet = mutableSeptet("a", "b", "c", "d", "e", "f", "g");
		septet.first("h");
		assertThat(septet.first()).isEqualTo("h");
	}

	@Test @DisplayName("factory methods return correct values")
	void factoryMethodsReturnCorrectValues() {
		var immutable = immutableSeptet("a", "b", "c", "d", "e", "f", "g");
		var mutable = mutableSeptet("h", "i", "j", "k", "l", "m", "n");

		assertThat(immutable.first()).isEqualTo("a");
		assertThat(immutable.second()).isEqualTo("b");
		assertThat(immutable.third()).isEqualTo("c");
		assertThat(immutable.fourth()).isEqualTo("d");
		assertThat(immutable.fifth()).isEqualTo("e");
		assertThat(immutable.sixth()).isEqualTo("f");
		assertThat(immutable.seventh()).isEqualTo("g");

		assertThat(mutable.first()).isEqualTo("h");
		assertThat(mutable.second()).isEqualTo("i");
		assertThat(mutable.third()).isEqualTo("j");
		assertThat(mutable.fourth()).isEqualTo("k");
		assertThat(mutable.fifth()).isEqualTo("l");
		assertThat(mutable.sixth()).isEqualTo("m");
		assertThat(mutable.seventh()).isEqualTo("n");
	}
}