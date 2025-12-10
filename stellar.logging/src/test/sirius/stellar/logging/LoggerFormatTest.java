package sirius.stellar.logging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.*;
import static sirius.stellar.logging.LoggerFormat.*;

final class LoggerFormatTest {

    //#region format(String, Object...)
    @Test @DisplayName("format(String, Object...) invokes MessageFormat")
    void formatInvokesMessageFormat() {
        var format = "Lorem {0} dolor sit amet.";
        var arguments = new Object[]{"ipsum"};

        var result = format(format, arguments);

        assertThat(result).isEqualTo("Lorem ipsum dolor sit amet.");
    }

    @Test @DisplayName("format(String, Object...) invokes String#format")
    void formatInvokesStringFormat() {
        var format = "Lorem %s dolor sit amet.";
        var arguments = new Object[]{"ipsum"};

        var result = format(format, arguments);

        assertThat(result).isEqualTo("Lorem ipsum dolor sit amet.");
    }

    @Test @DisplayName("format(String, Object...) with null inputs doesn't throw")
    void formatWithNullInputsDoesNotThrow() {
		assertThatNoException().isThrownBy(() -> {
			format(null, (Object[]) null);
		});
    }

    @Test @DisplayName("format(String, Object...) with null formatting arguments doesn't throw")
    void formatWithNullFormattingArgumentsDoesNotThrow() {
        assertThatNoException().isThrownBy(() -> {
        	var format = "Lorem {1} dolor sit amet.";
	        var arguments = new Object[]{null, "ipsum"};

			format(format, arguments);
		});
    }
    //#endregion

    //#region format(Locale, String, Object...)
    @Test @DisplayName("format(Locale, String, Object...) invokes MessageFormat")
    void formatWithLocaleInvokesMessageFormat() {
        var locale = Locale.ENGLISH;
        var format = "Lorem {0} dolor sit amet.";
        var arguments = new Object[]{"ipsum"};

        var result = format(locale, format, arguments);

        assertThat(result).isEqualTo("Lorem ipsum dolor sit amet.");
    }

    @Test @DisplayName("format(Locale, String, Object...) invokes String#format")
    void formatWithLocaleInvokesStringFormat() {
        var locale = Locale.ENGLISH;
        var format = "Lorem %s dolor sit amet.";
        var arguments = new Object[]{"ipsum"};

        var result = format(locale, format, arguments);

        assertThat(result).isEqualTo("Lorem ipsum dolor sit amet.");
    }

    @Test @DisplayName("format(Locale, String, Object...) with null inputs doesn't throw NullPointerException")
    void formatWithLocaleAndNullInputsDoesNotThrow() {
		assertThatNoException().isThrownBy(() -> {
			format(null, null, (Object[]) null);
		});
    }

    @Test @DisplayName("format(Locale, String, Object...) with null formatting arguments doesn't throw NullPointerException")
    void formatWithLocaleAndNullFormattingArgumentsDoesNotThrow() {
		assertThatNoException().isThrownBy(() -> {
			var format = "Lorem {1} dolor sit amet.";
			var arguments = new String[]{null, "ipsum"};

			format(format, (Object[]) arguments);
		});
    }

    @Test @DisplayName("format(Locale, String, Object...) correctly formats with locale")
    void formatWithLocaleCorrectlyFormats() {
        var locale = Locale.GERMAN;
        var format = "Foobar costs {0}";
        var arguments = new Object[]{123_456.789};

        var result = format(locale, format, arguments);

        assertThat(result).isEqualTo("Foobar costs 123.456,789");
    }
    //#endregion
}