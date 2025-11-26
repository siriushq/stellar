package sirius.stellar.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static sirius.stellar.configuration.Configuration.*;

final class SystemConfigurationProviderTest extends AbstractConfigurationTest {

	@Test @DisplayName("SystemConfigurationProvider: all property access methods correctly function")
	void access() {
		System.getProperties().putAll(Map.of(
			"EXAMPLE_STRING", "Hello, world!",
			"EXAMPLE_BOOLEAN", "true",
			"EXAMPLE_INTEGER", "123",
			"EXAMPLE_LONG", String.valueOf(Long.MAX_VALUE),
			"EXAMPLE_MAPPED", "9999" + Long.MAX_VALUE
		));

		this.reset();

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
}