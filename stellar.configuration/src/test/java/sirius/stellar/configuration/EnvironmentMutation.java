package sirius.stellar.configuration;

import org.junit.platform.commons.PreconditionViolationException;

import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
final class EnvironmentMutation {

	/// Convenience method for [#environment(Map)] with just one entry.
	/// @see #environment(Map)
	static void environment(String key, String value) {
		environment(Map.of(key, value));
	}

	/// Append the provided entries to the environment variables of the
	/// current JVM using reflection, ignoring the current test upon failure.
	static void environment(Map<String, String> entries) {
		Optional<ReflectiveOperationException> optional;

		optional = environmentProcessEnvironment(entries);
		if (optional.isEmpty()) return;

		optional = environmentSystemGetenv(entries);
		if (optional.isEmpty()) return;

		ReflectiveOperationException exception = optional.get();
		throw new PreconditionViolationException("Failed overriding System#getenv", exception);
	}

	/// Used by [#environment(Map)] to set environment variables by
	/// attempting to modify `java.lang.ProcessEnvironment`.
	private static Optional<ReflectiveOperationException>
	environmentProcessEnvironment(Map<String, String> entries) {
		try {
			var clazz = Class.forName("java.lang.ProcessEnvironment");

			var field = clazz.getDeclaredField("theEnvironment");
			field.setAccessible(true);

			var writable = (Map<String, String>) field.get(null);
			writable.putAll(entries);

			field = clazz.getDeclaredField("theCaseInsensitiveEnvironment");
			field.setAccessible(true);

			writable = (Map<String, String>) field.get(null);
			writable.putAll(entries);
			return Optional.empty();
		} catch (ReflectiveOperationException exception) {
			return Optional.of(exception);
		}
	}

	/// Used by [#environment(Map)] to set environment variables by attempting
	/// to modify `System.getenv()` by expecting a private map field `m` in
	/// a returned `java.util.Collections$UnmodifiableMap`.
	private static Optional<ReflectiveOperationException>
	environmentSystemGetenv(Map<String, String> entries) {
		try {
			var env = System.getenv();

			var field = env.getClass().getDeclaredField("m");
			field.setAccessible(true);

			var writable = (Map<String, String>) field.get(env);
			writable.putAll(entries);
			return Optional.empty();
		} catch (ReflectiveOperationException exception) {
			return Optional.of(exception);
		}
	}
}