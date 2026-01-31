package sirius.stellar.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static sirius.stellar.configuration.Configuration.property;
import static sirius.stellar.configuration.mutator.ConfigurationMutator.reset;

final class ConfigurationProviderTest {

	@Test @DisplayName("ConfigurationProvider: correct handling of preceding providers")
	void preceding() throws IOException {
		var path = Path.of("./example.properties");

		deleteIfExists(path);
		var file = createFile(path);

		writeString(file, "EXAMPLE_KEY=Hello, from properties file!");
		System.getProperties().put("EXAMPLE_KEY", "Hello, from JVM system properties!");

		reset();

		assertSoftly(softly -> {
			var myKey = property("EXAMPLE_KEY");
			softly.assertThat(myKey).isEqualTo("Hello, from properties file!");

			softly.assertThatCode(() -> deleteIfExists(file)).doesNotThrowAnyException();
		});
	}
}