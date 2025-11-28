package sirius.stellar.configuration.jshell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sirius.stellar.configuration.testing.AbstractConfigurationTest;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.*;
import static org.assertj.core.api.SoftAssertions.*;
import static sirius.stellar.configuration.Configuration.*;

final class JShellConfigurationProviderTest extends AbstractConfigurationTest {

	@Test @DisplayName("JShellConfigurationProvider: all property access methods correctly function")
	void access() throws IOException {
		var path = Path.of("./example.jsh");
		var script = "Map.of(\"EXAMPLE_KEY\", \"EXAMPLE_VALUE\");";

		deleteIfExists(path);
		var file = createFile(path);
		writeString(file, script);

		this.reset();

		assertSoftly(softly -> {
			var myKey = property("EXAMPLE_KEY");
			softly.assertThat(myKey).isEqualTo("EXAMPLE_VALUE");

			softly.assertThatCode(() -> deleteIfExists(file)).doesNotThrowAnyException();
		});
	}
}