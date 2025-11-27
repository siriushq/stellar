package sirius.stellar.configuration.testing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.*;
import static org.assertj.core.api.Assertions.*;
import static sirius.stellar.configuration.Configuration.*;

final class ConfigurationProviderTest extends AbstractConfigurationTest {

	@Test @DisplayName("ConfigurationProvider: correct handling of preceding providers")
	void preceding() throws IOException {
		var path = Path.of("./example.properties");

		deleteIfExists(path);
		var file = createFile(path);

		writeString(file, "EXAMPLE_KEY=Hello, from properties file!");
		System.getProperties().put("EXAMPLE_KEY", "Hello, from JVM system properties!");

		this.reset();

		var myKey = property("EXAMPLE_KEY");
		assertThat(myKey).isEqualTo("Hello, from properties file!");
	}
}