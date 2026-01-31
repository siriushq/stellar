package sirius.stellar.configuration.yaml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.*;
import static java.text.MessageFormat.format;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static sirius.stellar.configuration.Configuration.*;
import static sirius.stellar.configuration.mutator.ConfigurationMutator.reset;

final class YamlConfigurationProviderTest {

	@Test @DisplayName("YamlConfigurationProvider: all property access methods correctly function")
	void access() throws IOException {
		var path = Path.of("./example.yml");
		var toml = """
		example-top-level: "Welcome"

		basic:
		  example-string: "Hello, world!"
		  example-boolean: true
		  example-integer: 123
		  example-long: {0,number,#}
		  example-array: [1, 2, 3]
		""";

		deleteIfExists(path);
		var file = createFile(path);
		writeString(file, format(toml.stripIndent(), Long.MAX_VALUE));

		reset();

		assertSoftly(softly -> {
			var myTopLevel = property("example-top-level");

			var myString = property("basic.example-string");
			var myBoolean = propertyBoolean("basic.example-boolean");
			var myInteger = propertyInteger("basic.example-integer");
			var myLong = propertyLong("basic.example-long");
			var myArray = propertyAs("basic.example-array", string -> string.split(";"));

			softly.assertThat(myTopLevel).isEqualTo("Welcome");
			softly.assertThat(myString).isEqualTo("Hello, world!");
			softly.assertThat(myBoolean).isTrue();
			softly.assertThat(myInteger).isEqualTo(123);
			softly.assertThat(myLong).isEqualTo(Long.MAX_VALUE);
			softly.assertThat(myArray).containsExactly("1", "2", "3");

			softly.assertThatCode(() -> deleteIfExists(file)).doesNotThrowAnyException();
		});
	}
}