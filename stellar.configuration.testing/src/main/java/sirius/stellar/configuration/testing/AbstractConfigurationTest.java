package sirius.stellar.configuration.testing;

import sirius.stellar.configuration.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/// Base class for all tests of [Configuration].
/// This provides lifecycle management to safely reset state between tests.
public abstract class AbstractConfigurationTest {

	/// Reset/re-initialize internal state of [Configuration] with reflection.
	///
	/// Reflection is used to maintain the encapsulated contract of this internal state,
	/// while allowing tests to be performed which need to re-run the static initialization
	/// of this system.
	protected void reset() {
		try {
			Class<Configuration> clazz = Configuration.class;

			Field configuration = clazz.getDeclaredField("configuration");
			configuration.setAccessible(true);
			configuration.set(null, new HashMap<>());

			Field providers = clazz.getDeclaredField("providers");
			providers.setAccessible(true);
			providers.set(null, new LinkedList<>());

			Field bindings = clazz.getDeclaredField("bindings");
			bindings.setAccessible(true);
			bindings.set(null, new HashSet<>());

			Method load = clazz.getDeclaredMethod("load");
			load.setAccessible(true);
			load.invoke(null);
		} catch (NoSuchFieldException | NoSuchMethodException
				 | IllegalAccessException | InvocationTargetException exception) {
			throw new IllegalStateException("Failed reflectively resetting configuration state for test", exception);
		}
	}
}