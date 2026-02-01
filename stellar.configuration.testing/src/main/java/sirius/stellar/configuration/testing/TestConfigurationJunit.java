package sirius.stellar.configuration.testing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static sirius.stellar.configuration.mutator.ConfigurationMutator.reset;
import static sirius.stellar.configuration.testing.TestConfigurationState.configuration;

/// Implementation of JUnit [Extension] which runs if any test class or
/// [Test] method is annotated with [TestConfiguration], populating the
/// declared key-value pairs on [TestConfigurationState].
///
/// @since 1.0
public final class TestConfigurationJunit
	implements BeforeAllCallback, BeforeEachCallback {

	@Override
	public void beforeAll(ExtensionContext context) {
		configuration.clear();
	}

	@Override
	public void beforeEach(ExtensionContext context) {
		Optional<Map<String, String>> global = context.getTestClass()
				.map(clazz -> clazz.getAnnotation(TestConfiguration.class))
				.map(annotation -> map(annotation.value()));

		if (global.isEmpty()) return;
		populate(context, global.get());

		Optional<Map<String, String>> specific = context.getTestMethod()
				.map(method -> method.getAnnotation(TestConfiguration.class))
				.map(annotation -> map(annotation.value()));

		if (specific.isEmpty()) return;
		populate(context, specific.get());
	}

	/// Populate [TestConfigurationState] with all the provided values
	/// and attempt to reflectively reinitialize, only warning failures.
	private static void
	populate(ExtensionContext context, Map<String, String> values) {
		try {
			configuration.putAll(values);
			reset();
		} catch (IllegalStateException exception) {
			context.publishReportEntry(exception.getMessage());
			context.publishReportEntry(exception.getCause().getMessage());
		}
	}

	/// Convert a `String[]` to a map of keys and values, expecting the
	/// array to contain a sequence of keys followed by values.
	private static Map<String, String> map(String[] pairs) {
		Map<String, String> map = new HashMap<>();

		for (int i = 0; i < pairs.length; i += 2) {
			String key = pairs[i];
			String value = pairs[i + 1];

			map.put(key, value);
		}

		return map;
	}
}