package sirius.stellar.configuration.testing;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.configuration.Configuration.property;
import static sirius.stellar.configuration.testing.TestConfigurationState.put;

@DisplayNameGeneration(ReplaceUnderscores.class)
final class TestForTestConfigurationState {

	static {
		put("MY_KEY", "MY_VALUE");
	}

	@Test
	void static_initializer_put() {
		assertThat(property("MY_KEY"))
			.isEqualTo("MY_VALUE");
	}
}