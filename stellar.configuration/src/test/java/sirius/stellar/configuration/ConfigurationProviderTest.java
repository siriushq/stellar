package sirius.stellar.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.configuration.Configuration.property;
import static sirius.stellar.configuration.EnvironmentMutation.environment;
import static sirius.stellar.configuration.mutator.ConfigurationMutator.reset;

final class ConfigurationProviderTest {

	@Test @DisplayName("ConfigurationProvider: correct handling of preceding providers")
	void preceding() {
		var key = "EXAMPLE_KEY";

		if (!environment(key, "Hello, from environment variables!")) return;
		System.getProperties().put(key, "Hello, from JVM system properties!");

		reset();

		assertThat(property(key))
			.isEqualTo("Hello, from environment variables!");
	}
}