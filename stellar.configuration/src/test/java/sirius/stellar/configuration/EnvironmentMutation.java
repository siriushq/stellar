package sirius.stellar.configuration;

import java.lang.reflect.InaccessibleObjectException;
import java.util.Map;

import static java.lang.System.err;

@SuppressWarnings("unchecked")
final class EnvironmentMutation {

	/// Convenience method for [#environment(Map)] with just one entry.
	/// @see #environment(Map)
	static boolean environment(String key, String value) {
		return environment(Map.of(key, value));
	}

	/// Append the provided entries to the environment variables of the
	/// current JVM with reflection.
	static boolean environment(Map<String, String> entries) {
		try {
			var env = System.getenv();

			var field = env.getClass().getDeclaredField("m");
			field.setAccessible(true);

			var writable = (Map<String, String>) field.get(env);
			writable.putAll(entries);
			return true;
		} catch (NoSuchFieldException
				 | IllegalAccessException
				 | InaccessibleObjectException exception) {
			err.printf("Failed to override System#getenv, skipping test (%s)%n", exception);
			return false;
		}
	}
}