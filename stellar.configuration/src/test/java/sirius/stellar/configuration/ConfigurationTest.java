package sirius.stellar.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InaccessibleObjectException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Map;

import static java.lang.System.*;
import static java.nio.file.Files.*;
import static java.text.MessageFormat.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static sirius.stellar.configuration.Configuration.*;

final class ConfigurationTest {

	@Test @DisplayName("EnvironmentConfigurationProvider: all property access methods correctly function")
	void propertyAccessWithEnvironmentConfigurationProvider() {
		if (!this.modifyEnvironment(Map.of(
			"EXAMPLE_STRING", "Hello, world!",
			"EXAMPLE_BOOLEAN", "true",
			"EXAMPLE_INTEGER", "123",
			"EXAMPLE_LONG", String.valueOf(Long.MAX_VALUE),
			"EXAMPLE_MAPPED", "9999" + Long.MAX_VALUE
		))) return;

		assertThatNoException().isThrownBy(Configuration::load);

		var myString = property("EXAMPLE_STRING");
		var myBoolean = propertyBoolean("EXAMPLE_BOOLEAN");
		var myInteger = propertyInteger("EXAMPLE_INTEGER");
		var myLong = propertyLong("EXAMPLE_LONG");
		var myMapped = propertyAs("EXAMPLE_MAPPED", BigInteger::new);

		assertSoftly(softly -> {
			softly.assertThat(myString).isEqualTo("Hello, world!");
			softly.assertThat(myBoolean).isEqualTo(true);
			softly.assertThat(myInteger).isEqualTo(123);
			softly.assertThat(myLong).isEqualTo(Long.MAX_VALUE);
			softly.assertThat(myMapped).isEqualTo(new BigInteger("9999" + Long.MAX_VALUE));
		});
	}

	@Test @DisplayName("SystemConfigurationProvider: all property access methods correctly function")
	void propertyAccessWithSystemConfigurationProvider() {
		System.getProperties().putAll(Map.of(
			"EXAMPLE_STRING", "Hello, world!",
			"EXAMPLE_BOOLEAN", "true",
			"EXAMPLE_INTEGER", "123",
			"EXAMPLE_LONG", String.valueOf(Long.MAX_VALUE),
			"EXAMPLE_MAPPED", "9999" + Long.MAX_VALUE
		));

		assertThatNoException().isThrownBy(Configuration::load);

		var myString = property("EXAMPLE_STRING");
		var myBoolean = propertyBoolean("EXAMPLE_BOOLEAN");
		var myInteger = propertyInteger("EXAMPLE_INTEGER");
		var myLong = propertyLong("EXAMPLE_LONG");
		var myMapped = propertyAs("EXAMPLE_MAPPED", BigInteger::new);

		assertSoftly(softly -> {
			softly.assertThat(myString).isEqualTo("Hello, world!");
			softly.assertThat(myBoolean).isEqualTo(true);
			softly.assertThat(myInteger).isEqualTo(123);
			softly.assertThat(myLong).isEqualTo(Long.MAX_VALUE);
			softly.assertThat(myMapped).isEqualTo(new BigInteger("9999" + Long.MAX_VALUE));
		});
	}

	@Test @DisplayName("PropertiesConfigurationProvider: all property access methods correctly function")
	void propertyAccessWithPropertiesConfigurationProvider() throws IOException {
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

		assertThatNoException().isThrownBy(Configuration::load);

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

	/// Append the provided entries to the environment variables of the current JVM with reflection.
	boolean modifyEnvironment(Map<String, String> entries) {
		try {
			var env = System.getenv();

			var field = env.getClass().getDeclaredField("m");
			field.setAccessible(true);

			var writable = (Map<String, String>) field.get(env);
			writable.putAll(entries);
			return true;
		} catch (NoSuchFieldException | IllegalAccessException | InaccessibleObjectException exception) {
			err.printf("Failed to override System#getenv, skipping test (%s)\n", exception);
			return false;
		}
	}
}