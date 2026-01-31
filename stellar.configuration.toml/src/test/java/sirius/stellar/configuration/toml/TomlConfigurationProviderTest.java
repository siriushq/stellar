package sirius.stellar.configuration.toml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import static java.nio.file.Files.*;
import static java.text.MessageFormat.format;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static sirius.stellar.configuration.Configuration.*;
import static sirius.stellar.configuration.mutator.ConfigurationMutator.reset;

final class TomlConfigurationProviderTest {

	@Test @DisplayName("TomlConfigurationProvider: all property access methods correctly function")
	void access() throws IOException {
		var path = Path.of("./example.toml");
		var toml = """
		example_top_level = "Welcome"

		[basic]
		example_string = "Hello, world!"
		example_boolean = true
		example_integer = 123
		example_long = {0,number,#}
		example_array = [1, 2, 3]

		[clock]
		example_local_datetime = 2024-09-09T15:30:00
		example_local_date = 2024-09-09
		example_local_time = 15:30:00
		example_offset_datetime = 2024-09-09T15:30:00+02:00
		""";

		deleteIfExists(path);
		var file = createFile(path);
		writeString(file, format(toml.stripIndent(), Long.MAX_VALUE));

		reset();

		assertSoftly(softly -> {
			var myTopLevel = property("example_top_level");

			var myString = property("basic.example_string");
			var myBoolean = propertyBoolean("basic.example_boolean");
			var myInteger = propertyInteger("basic.example_integer");
			var myLong = propertyLong("basic.example_long");
			var myArray = propertyAs("basic.example_array", string -> string.split(";"));

			var myLocalDateTime = propertyAs("clock.example_local_datetime", LocalDateTime::parse);
			var myLocalDate = propertyAs("clock.example_local_date", LocalDate::parse);
			var myLocalTime = propertyAs("clock.example_local_time", LocalTime::parse);
			var myOffsetDateTime = propertyAs("clock.example_offset_datetime", ZonedDateTime::parse);

			softly.assertThat(myTopLevel).isEqualTo("Welcome");
			softly.assertThat(myString).isEqualTo("Hello, world!");
			softly.assertThat(myBoolean).isTrue();
			softly.assertThat(myInteger).isEqualTo(123);
			softly.assertThat(myLong).isEqualTo(Long.MAX_VALUE);
			softly.assertThat(myArray).containsExactly("1", "2", "3");

			softly.assertThat(myLocalDateTime).isEqualTo(LocalDateTime.of(2024, 9, 9, 15, 30, 0));
			softly.assertThat(myLocalDate).isEqualTo(LocalDate.of(2024, 9, 9));
			softly.assertThat(myLocalTime).isEqualTo(LocalTime.of(15, 30, 0));
			softly.assertThat(myOffsetDateTime).isEqualTo(ZonedDateTime.parse("2024-09-09T15:30:00+02:00"));

			softly.assertThatCode(() -> deleteIfExists(file)).doesNotThrowAnyException();
		});
	}
}