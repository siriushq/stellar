package sirius.stellar.configuration;

import java.lang.reflect.InaccessibleObjectException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import static java.lang.System.*;
import static org.assertj.core.api.Assertions.*;
import static sirius.stellar.configuration.Configuration.*;
import static sirius.stellar.configuration.Configuration.load;

/// Base class for all tests of [Configuration].
/// This provides lifecycle management to safely reset state between tests.
abstract class AbstractConfigurationTest {

	/// Reset/re-initialize internal state of [Configuration].
	protected void reset() {
		assertThatNoException().isThrownBy(() -> {
			configuration = new HashMap<>();
			providers = new LinkedList<>();
			bindings = new HashSet<>();
			load();
		});
	}

	/// Append the provided entries to the environment variables of the current JVM with reflection.
	@SuppressWarnings("unchecked")
	protected boolean environment(Map<String, String> entries) {
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