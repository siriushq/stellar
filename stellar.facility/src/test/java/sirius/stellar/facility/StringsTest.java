package sirius.stellar.facility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

final class StringsTest {

    //#region format(String, Object...)
    @Test @DisplayName("format(String, Object...) invokes MessageFormat")
    void formatInvokesMessageFormat() {
        var format = "Lorem {0} dolor sit amet.";
        var arguments = new Object[]{"ipsum"};

        var result = Strings.format(format, arguments);

        assertThat(result).isEqualTo("Lorem ipsum dolor sit amet.");
    }

    @Test @DisplayName("format(String, Object...) invokes String#format")
    void formatInvokesStringFormat() {
        var format = "Lorem %s dolor sit amet.";
        var arguments = new Object[]{"ipsum"};

        var result = Strings.format(format, arguments);

        assertThat(result).isEqualTo("Lorem ipsum dolor sit amet.");
    }

    @Test @DisplayName("format(String, Object...) with null inputs doesn't throw")
    void formatWithNullInputsDoesNotThrow() {
		assertThatNoException().isThrownBy(() -> {
			Strings.format(null, (Object[]) null);
		});
    }

    @Test @DisplayName("format(String, Object...) with null formatting arguments doesn't throw")
    void formatWithNullFormattingArgumentsDoesNotThrow() {
        assertThatNoException().isThrownBy(() -> {
        	var format = "Lorem {1} dolor sit amet.";
	        var arguments = new Object[]{null, "ipsum"};

			Strings.format(format, arguments);
		});
    }
    //#endregion

    //#region format(Locale, String, Object...)
    @Test @DisplayName("format(Locale, String, Object...) invokes MessageFormat")
    void formatWithLocaleInvokesMessageFormat() {
        var locale = Locale.ENGLISH;
        var format = "Lorem {0} dolor sit amet.";
        var arguments = new Object[]{"ipsum"};

        var result = Strings.format(locale, format, arguments);

        assertThat(result).isEqualTo("Lorem ipsum dolor sit amet.");
    }

    @Test @DisplayName("format(Locale, String, Object...) invokes String#format")
    void formatWithLocaleInvokesStringFormat() {
        var locale = Locale.ENGLISH;
        var format = "Lorem %s dolor sit amet.";
        var arguments = new Object[]{"ipsum"};

        var result = Strings.format(locale, format, arguments);

        assertThat(result).isEqualTo("Lorem ipsum dolor sit amet.");
    }

    @Test @DisplayName("format(Locale, String, Object...) with null inputs doesn't throw NullPointerException")
    void formatWithLocaleAndNullInputsDoesNotThrow() {
		assertThatNoException().isThrownBy(() -> {
			Strings.format(null, null, (Object[]) null);
		});
    }

    @Test @DisplayName("format(Locale, String, Object...) with null formatting arguments doesn't throw NullPointerException")
    void formatWithLocaleAndNullFormattingArgumentsDoesNotThrow() {
		assertThatNoException().isThrownBy(() -> {
			var format = "Lorem {1} dolor sit amet.";
			var arguments = new String[]{null, "ipsum"};

			Strings.format(format, (Object[]) arguments);
		});
    }

    @Test @DisplayName("format(Locale, String, Object...) correctly formats with locale")
    void formatWithLocaleCorrectlyFormats() {
        var locale = Locale.GERMAN;
        var format = "Foobar costs {0}";
        var arguments = new Object[]{123_456.789};

        var result = Strings.format(locale, format, arguments);

        assertThat(result).isEqualTo("Foobar costs 123.456,789");
    }
    //#endregion

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