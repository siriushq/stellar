package sirius.stellar.facility.tuple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import sirius.stellar.facility.exception.ImmutableModificationException;

import static org.assertj.core.api.Assertions.*;
import static sirius.stellar.facility.tuple.Quintet.*;

final class QuintetTest {

	@Test @DisplayName("immutableQuintet throws ImmutableModificationException on modification")
	void immutableQuintetThrowsOnModification() {
		var quintet = immutableQuintet("a", "b", "c", "d", "e");
		assertThatThrownBy(() -> quintet.first("f")).isInstanceOf(ImmutableModificationException.class);
	}

	@Test @DisplayName("mutableQuintet allows modification")
	void mutableQuintetAllowsModification() {
		var quintet = mutableQuintet("a", "b", "c", "d", "e");
		quintet.first("f");
		assertThat(quintet.first()).isEqualTo("f");
	}

	@Test @DisplayName("factory methods return correct values")
	void factoryMethodsReturnCorrectValues() {
		var immutable = immutableQuintet("a", "b", "c", "d", "e");
		var mutable = mutableQuintet("f", "g", "h", "i", "j");

		assertThat(immutable.first()).isEqualTo("a");
		assertThat(immutable.second()).isEqualTo("b");
		assertThat(immutable.third()).isEqualTo("c");
		assertThat(immutable.fourth()).isEqualTo("d");
		assertThat(immutable.fifth()).isEqualTo("e");

		assertThat(mutable.first()).isEqualTo("f");
		assertThat(mutable.second()).isEqualTo("g");
		assertThat(mutable.third()).isEqualTo("h");
		assertThat(mutable.fourth()).isEqualTo("i");
		assertThat(mutable.fifth()).isEqualTo("j");
	}
}