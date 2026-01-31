package sirius.stellar.configuration.mutator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import static java.lang.Class.forName;

/// Shared utility class for the testing of developed configuration extensions,
/// modifying immutable state of `sirius.stellar.configuration.Configuration`.
///
/// This provides lifecycle management to safely reset state between tests.
/// This should never be used in non-testing code.
public interface ConfigurationMutator {

	/// Reset/re-initialize internal state with reflection.
	///
	/// Reflection is intentionally used to maintain the encapsulated contract
	/// of this internal state, while allowing tests to be performed (which need
	/// to re-run the static initialization of this system).
	static void reset() {
		try {
			Class<?> clazz = forName("sirius.stellar.configuration.Configuration");

			Field configuration = clazz.getDeclaredField("configuration");
			configuration.setAccessible(true);
			configuration.set(null, new HashMap<>());

			Field providers = clazz.getDeclaredField("providers");
			providers.setAccessible(true);
			providers.set(null, new LinkedList<>());

			Field bindings = clazz.getDeclaredField("bindings");
			bindings.setAccessible(true);
			bindings.set(null, new HashSet<>());

			Method initialize = clazz.getDeclaredMethod("initialize");
			initialize.setAccessible(true);
			initialize.invoke(null);
		} catch (NoSuchFieldException | NoSuchMethodException
				 | IllegalAccessException | InvocationTargetException
				 | ClassNotFoundException exception) {
			throw new IllegalStateException("Failed reflectively resetting configuration state for test", exception);
		}
	}
}