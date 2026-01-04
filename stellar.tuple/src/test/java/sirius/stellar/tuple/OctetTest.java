package sirius.stellar.tuple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static sirius.stellar.tuple.Octet.immutableOctet;
import static sirius.stellar.tuple.Octet.mutableOctet;

final class OctetTest {

	@Test @DisplayName("immutableOctet throws UnsupportedOperationException on modification")
	void immutableOctetThrowsOnModification() {
		var octet = immutableOctet("a", "b", "c", "d", "e", "f", "g", "h");
		assertThatThrownBy(() -> octet.first("i")).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test @DisplayName("mutableOctet allows modification")
	void mutableOctetAllowsModification() {
		var octet = mutableOctet("a", "b", "c", "d", "e", "f", "g", "h");
		octet.first("i");
		assertThat(octet.first()).isEqualTo("i");
	}

	@Test @DisplayName("factory methods return correct values")
	void factoryMethodsReturnCorrectValues() {
		var immutable = immutableOctet("a", "b", "c", "d", "e", "f", "g", "h");
		var mutable = mutableOctet("i", "j", "k", "l", "m", "n", "o", "p");

		assertThat(immutable.first()).isEqualTo("a");
		assertThat(immutable.second()).isEqualTo("b");
		assertThat(immutable.third()).isEqualTo("c");
		assertThat(immutable.fourth()).isEqualTo("d");
		assertThat(immutable.fifth()).isEqualTo("e");
		assertThat(immutable.sixth()).isEqualTo("f");
		assertThat(immutable.seventh()).isEqualTo("g");
		assertThat(immutable.eighth()).isEqualTo("h");

		assertThat(mutable.first()).isEqualTo("i");
		assertThat(mutable.second()).isEqualTo("j");
		assertThat(mutable.third()).isEqualTo("k");
		assertThat(mutable.fourth()).isEqualTo("l");
		assertThat(mutable.fifth()).isEqualTo("m");
		assertThat(mutable.sixth()).isEqualTo("n");
		assertThat(mutable.seventh()).isEqualTo("o");
		assertThat(mutable.eighth()).isEqualTo("p");
	}
}