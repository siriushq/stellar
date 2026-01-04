package sirius.stellar.tuple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static sirius.stellar.tuple.Quartet.immutableQuartet;
import static sirius.stellar.tuple.Quartet.mutableQuartet;

final class QuartetTest {

	@Test @DisplayName("immutableQuartet throws UnsupportedOperationException on modification")
	void immutableQuartetThrowsOnModification() {
		var quartet = immutableQuartet("a", "b", "c", "d");
		assertThatThrownBy(() -> quartet.first("e")).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test @DisplayName("mutableQuartet allows modification")
	void mutableQuartetAllowsModification() {
		var quartet = mutableQuartet("a", "b", "c", "d");
		quartet.first("e");
		assertThat(quartet.first()).isEqualTo("e");
	}

	@Test @DisplayName("factory methods return correct values")
	void factoryMethodsReturnCorrectValues() {
		var immutableQuartet = immutableQuartet("a", "b", "c", "d");
		var mutableQuartet = mutableQuartet("e", "f", "g", "h");

		assertThat(immutableQuartet.first()).isEqualTo("a");
		assertThat(immutableQuartet.second()).isEqualTo("b");
		assertThat(immutableQuartet.third()).isEqualTo("c");
		assertThat(immutableQuartet.fourth()).isEqualTo("d");

		assertThat(mutableQuartet.first()).isEqualTo("e");
		assertThat(mutableQuartet.second()).isEqualTo("f");
		assertThat(mutableQuartet.third()).isEqualTo("g");
		assertThat(mutableQuartet.fourth()).isEqualTo("h");
	}
}