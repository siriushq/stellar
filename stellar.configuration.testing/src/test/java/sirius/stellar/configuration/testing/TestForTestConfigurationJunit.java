package sirius.stellar.configuration.testing;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.configuration.Configuration.property;

@TestConfiguration({
	"MY_KEY", "MY_VALUE"
})
@DisplayNameGeneration(ReplaceUnderscores.class)
final class TestForTestConfigurationJunit {

	@Test
	void global_class_annotation() {
		assertThat(property("MY_KEY"))
			.isEqualTo("MY_VALUE");

		assertThat(property("MY_KEY_2"))
			.isEqualTo("");
	}

	@Test
	@TestConfiguration({
		"MY_KEY", "MY_OVERRIDE",
		"MY_KEY_2", "MY_VALUE_2"
	})
	void specific_method_annotation() {
		assertThat(property("MY_KEY"))
			.isEqualTo("MY_OVERRIDE");

		assertThat(property("MY_KEY_2"))
			.isEqualTo("MY_VALUE_2");
	}
}