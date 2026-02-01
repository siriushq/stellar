package sirius.stellar.configuration.mutator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import static java.lang.Class.forName;

/// This interface consists exclusively of static methods for the testing of
/// developed configuration extensions, and any other purpose where modifying
/// the immutable state of `sirius.stellar.configuration.Configuration` is
/// desired. This should never be used in non-testing code.
///
/// @since 1.0
public interface ConfigurationMutator {

	/// Reset/re-initialize internal state with reflection.
	/// @since 1.0
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
			throw new IllegalStateException("Failed reflective reinitialization", exception);
		}
	}
}