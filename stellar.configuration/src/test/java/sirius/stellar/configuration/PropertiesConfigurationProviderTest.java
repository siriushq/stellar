package sirius.stellar.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;

import static java.nio.file.Files.*;
import static java.text.MessageFormat.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static sirius.stellar.configuration.Configuration.*;

final class PropertiesConfigurationProviderTest extends AbstractConfigurationTest {

	@Test @DisplayName("PropertiesConfigurationProvider: all property access methods correctly function")
	void access() throws IOException {
		var path = Path.of("./example.properties");
		var properties = """
		EXAMPLE_STRING=Hello, world!
		EXAMPLE_BOOLEAN=true
		EXAMPLE_INTEGER=123
		EXAMPLE_LONG={0,number,#}
		EXAMPLE_MAPPED=9999{0,number,#}
		""";

		deleteIfExists(path);
		var file = createFile(path);
		writeString(file, format(properties.stripIndent(), Long.MAX_VALUE));

		this.reset();

		assertSoftly(softly -> {
			var myString = property("EXAMPLE_STRING");
			var myBoolean = propertyBoolean("EXAMPLE_BOOLEAN");
			var myInteger = propertyInteger("EXAMPLE_INTEGER");
			var myLong = propertyLong("EXAMPLE_LONG");
			var myMapped = propertyAs("EXAMPLE_MAPPED", BigInteger::new);

			softly.assertThat(myString).isEqualTo("Hello, world!");
			softly.assertThat(myBoolean).isEqualTo(true);
			softly.assertThat(myInteger).isEqualTo(123);
			softly.assertThat(myLong).isEqualTo(Long.MAX_VALUE);
			softly.assertThat(myMapped).isEqualTo(new BigInteger("9999" + Long.MAX_VALUE));

			softly.assertThatCode(() -> deleteIfExists(file)).doesNotThrowAnyException();
		});
	}
}