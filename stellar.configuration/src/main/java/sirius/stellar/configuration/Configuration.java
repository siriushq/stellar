package sirius.stellar.configuration;

import java.util.*;
import java.util.function.Function;

import static java.lang.Boolean.*;
import static java.lang.Integer.*;
import static java.lang.Long.*;
import static java.util.Collections.*;

/// This class is the main entry-point for the configuration system.
///
/// Implementations of the service provider interface [ConfigurationProvider] are automagically
/// discovered on the module path and loaded by this class in order to instantiate configuration.
///
/// This module provides three default implementations, which are loaded automatically overriding
/// each other, in the guaranteed order specified below:
///
/// 1. [PropertiesConfigurationProvider], which loads all `.properties` files from the filesystem
///    relative to the working directory of the application, first placing authored template files
///    from any module on the module path.
/// 2. [EnvironmentConfigurationProvider], which loads from [System#getenv()].
/// 3. [SystemConfigurationProvider], which loads from [System#getProperties()].
///
/// The methods provided to access key/value pairs in the configuration are designed to be
/// statically-imported across an application, invoked as such: `println(property("EXAMPLE"))`.
public final class Configuration {

	private static Map<String, String> configuration = emptyMap();

	/// Static initializer block which automatically runs [Configuration#load()].
	static { load(); }

	/// Initializes the constant configuration map.
	/// This method is separate from the static initializer block for testability.
	static void load() {
		try {
			Map<String, String> map = new HashMap<>();

			ServiceLoader<ConfigurationProvider> loader = ServiceLoader.load(ConfigurationProvider.class);
			for (ConfigurationProvider provider : loader) map.putAll(provider.get());

			configuration = unmodifiableMap(map);
		} catch (Throwable throwable) {
			throw new IllegalStateException("Failed to wire configuration providers", throwable);
		}
	}

	/// Returns a [Map] of every initially loaded configuration key/value pair.
	public static Map<String, String> configurationMap() {
		return configuration;
	}

	/// Returns a [Properties] view of every initially loaded configuration key/value pair.
	public static Properties configurationProperties() {
		Properties properties = new Properties();
		properties.putAll(configuration);
		return properties;
	}

	//#region string properties
	/// Returns the value of the provided configuration key as a [String].
	public static String property(String key) {
		return property(key, "");
	}

	/// Returns the value of the provided configuration key as a [String] if available,
	/// otherwise returning the provided fallback value.
	public static String property(String key, String defaultValue) {
		return configuration.getOrDefault(key, defaultValue);
	}

	/// Returns the value of the provided configuration key optionally, as a [String].
	public static Optional<String> propertyOptional(String key) {
		return Optional.ofNullable(configuration.get(key));
	}
	//#endregion

	//#region boolean properties
	/// Returns the value of the provided configuration key as a `boolean`.
	/// @throws IllegalStateException if the configuration key is not set
	public static boolean propertyBoolean(String key) {
		String value = configuration.get(key);
		if (value == null || value.isBlank()) throw new IllegalStateException();
		return parseBoolean(value);
	}

	/// Returns the value of the provided configuration key as a `boolean` if available,
	/// otherwise returning the provided fallback value.
	public static boolean propertyBoolean(String key, boolean defaultValue) {
		String value = configuration.get(key);
		if (value == null || value.isBlank()) return defaultValue;
		return parseBoolean(value);
	}
	//#endregion

	//#region int properties
	/// Returns the value of the provided configuration key as an `int`.
	/// @throws IllegalStateException if the configuration key is not set
	/// @throws NumberFormatException failed to parse the set value
	public static int propertyInteger(String key) {
		String value = configuration.get(key);
		if (value == null || value.isBlank()) throw new IllegalStateException();
		return parseInt(value);
	}

	/// Returns the value of the provided configuration key as an `int` if available,
	/// otherwise returning the provided fallback value.
	/// @throws NumberFormatException failed to parse the set value
	public static int propertyInteger(String key, int defaultValue) {
		String value = configuration.get(key);
		if (value == null || value.isBlank()) return defaultValue;
		return parseInt(value);
	}
	//#endregion

	//#region long properties
	/// Returns the value of the provided configuration key as a `long`.
	/// @throws IllegalStateException if the configuration key is not set
	/// @throws NumberFormatException failed to parse the set value
	public static long propertyLong(String key) {
		String value = configuration.get(key);
		if (value == null || value.isBlank()) throw new IllegalStateException();
		return parseLong(value);
	}

	/// Returns the value of the provided configuration key as a `long` if available,
	/// otherwise returning the provided fallback value.
	/// @throws NumberFormatException failed to parse the set value
	public static long propertyLong(String key, long defaultValue) {
		String value = configuration.get(key);
		if (value == null || value.isBlank()) return defaultValue;
		return parseLong(value);
	}
	//#endregion

	//#region mapped properties
	/// Returns the value of the provided configuration key as `T` based on the provided
	/// mapping function that should return `T` from a [String] input.
	///
	/// For example, `propertyAs("EXAMPLE_BIGINTEGER", BigInteger::parseInt)`.
	/// @throws IllegalStateException if the configuration key is not set
	public static <T> T propertyAs(String key, Function<String, T> mapper) {
		String value = configuration.get(key);
		if (value == null || value.isBlank()) throw new IllegalStateException();
		return mapper.apply(value);
	}
	//#endregion
}