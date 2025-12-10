package sirius.stellar.facility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

final class StringsTest {

    //#region shuffle(Random, String)
    @Test @DisplayName("shuffle(Random, String) correctly shuffles string")
    void shuffleStringCorrectlyShuffles() {
        var random = new Random();
        var alphabet = "abcdefghijklmnopqrstuvwxyz";

        var result = Strings.shuffle(random, alphabet);

        assertThat(result).isNotEqualTo(alphabet);
    }

    @Test @DisplayName("shuffle(Random, String) with null random generator returns original string and doesn't throw")
    void shuffleStringWithNullRandomReturnsOriginal() {
        var string = "Hello, world!";
		assertThatNoException().isThrownBy(() -> {
			var result = Strings.shuffle(null, string);
			assertThat(result).isEqualTo(string);
		});
    }
    //#endregion

    //#region shuffle(Random, char[])
    @Test @DisplayName("shuffle(Random, char[]) correctly shuffles characters")
    void shuffleCharArrayCorrectlyShuffles() {
        var random = new Random();
        var alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

        var result = Strings.shuffle(random, alphabet);
        assertThat(result).isNotEqualTo(alphabet);
    }

    @Test @DisplayName("shuffle(Random, char[]) with null random generator returns original characters and doesn't throw")
    void shuffleCharArrayWithNullRandomReturnsOriginal() {
        var characters = "Hello, world!".toCharArray();
		assertThatNoException().isThrownBy(() -> {
			var result = Strings.shuffle(null, characters);
			assertThat(result).isEqualTo(characters);
		});
    }
    //#endregion
}