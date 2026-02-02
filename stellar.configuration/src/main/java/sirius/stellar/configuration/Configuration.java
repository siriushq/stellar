package sirius.stellar.configuration;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.util.Collections.*;
import static java.util.ServiceLoader.load;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/// This class is the main entry-point for the configuration system.
///
/// ### Value discovery
/// Implementations of the service provider interface [ConfigurationProvider]
/// are automagically discovered on the module path and loaded by this class
/// in order to instantiate configuration.
///
/// This module provides two default implementations that load automatically,
/// overriding each other, in the guaranteed order specified below:
///
/// 1. [EnvironmentConfigurationProvider], which loads from [System#getenv()].
/// 2. [SystemConfigurationProvider], which loads from [System#getProperties()].
///
/// ### Reloader discovery
/// Similarly to the above, [ConfigurationReloader] implementations are wired,
/// always after providers, and used to dispatch reload events and cause the
/// [ConfigurationProvider]s to be re-invoked, reading new values. No
/// default reloader implementations are imposed.
///
/// ### Usage
/// The methods provided to access key/value pairs in the configuration are
/// designed to be statically-imported across an application, invoked as such:
///
///     `println(property("EXAMPLE"))`.
///
/// The values returned can be assumed to be stable. Configuration reloading
/// is available, but only using explicit binding methods such as:
///
///     `propertyBinding("EXAMPLE", it -> println(it))`.
///
/// @since 1.0
public final class Configuration {

	static Map<String, String> configuration = new HashMap<>();
	static List<ConfigurationProvider> providers = new LinkedList<>();
	static Set<ConfigurationBinding<?>> bindings = new HashSet<>();

	static { initialize(); }

	/// Initializes the constant configuration map and wire all reloaders.
	/// This is automatically run by a static-initializer block.
	static void initialize() {
		Map<Class<?>, ConfigurationProvider> all = load(ConfigurationProvider.class)
			.stream()
			.map(ServiceLoader.Provider::get)
			.collect(toMap(ConfigurationProvider::clazz, identity()));

		Set<ConfigurationProvider> visiting = new HashSet<>();
		all.values().forEach(it -> visit(it, all, visiting));

		providers.stream()
			.map(ConfigurationProvider::get)
			.forEach(configuration::putAll);

		load(ConfigurationReloader.class)
			.stream()
			.map(ServiceLoader.Provider::get)
			.forEach(ConfigurationReloader::wire);

		configuration = unmodifiableMap(configuration);
		providers = unmodifiableList(providers);
		bindings = synchronizedSet(bindings);
	}

	/// Visitor for [ConfigurationProvider]s by their dependencies, recursively
	/// topological sorting via a depth-first search.
	///
	/// @see ConfigurationProvider#preceding()
	static <T extends ConfigurationProvider>
	void visit(T found, Map<Class<?>, T> all, Set<T> visiting) {
		if (!visiting.add(found)) return;

		for (Class<? extends ConfigurationProvider> dependency : found.preceding()) {
			T t = all.get(dependency);
			if (t == null) continue;
			visit(t, all, visiting);
		}

		providers.add(found);
	}

	/// Triggers a configuration reload (for all listeners).
	/// @see ConfigurationReloader#reload()
	static void reload() {
		Map<String, String> map = new HashMap<>();
		providers.stream()
				.map(ConfigurationProvider::get)
				.forEach(map::putAll);

		for (ConfigurationBinding<?> binding : bindings) {
			String key = binding.key();
			binding.update(map.get(key));
		}
	}

	/// Returns a [Map] of every initially loaded configuration key/value pair.
	/// @since 1.0
	public static Map<String, String> configurationMap() {
		return configuration;
	}

	/// Returns a [Properties] view of every initially loaded configuration key/value pair.
	/// @since 1.0
	public static Properties configurationProperties() {
		Properties properties = new Properties();
		properties.putAll(configuration);
		return properties;
	}

	//#region string properties
	/// Returns the value of the provided configuration key as a [String].
	/// @since 1.0
	public static String property(String key) {
		return property(key, "");
	}

	/// Returns the value of the provided configuration key as a [String]
	/// if available, otherwise returning the provided fallback value.
	/// @since 1.0
	public static String property(String key, String defaultValue) {
		return configuration.getOrDefault(key, defaultValue);
	}

	/// Returns the value of the provided configuration key optionally, as a [String].
	/// @since 1.0
	public static Optional<String> propertyOptional(String key) {
		return Optional.ofNullable(configuration.get(key));
	}

	/// Binds to the value of the provided configuration key as a [String].
	///
	/// The provided [Consumer] will be run first with the initial value, as returned
	/// by [#property(String)], and it will be re-run for any configuration reloads.
	///
	/// @since 1.0
	public static void propertyBinding(String key, Consumer<String> binding) {
		binding.accept(property(key));
		bindings.add(new ConfigurationBinding<>(key, String::valueOf, binding));
	}
	//#endregion

	//#region boolean properties
	/// Returns the value of the provided configuration key as a `boolean`.
	/// @throws IllegalStateException if the configuration key is not set
	/// @since 1.0
	public static boolean propertyBoolean(String key) {
		String value = configuration.get(key);
		if (value == null || value.isBlank()) throw new IllegalStateException();
		return parseBoolean(value);
	}

	/// Returns the value of the provided configuration key as a `boolean`
	/// if available, otherwise returning the provided fallback value.
	/// @since 1.0
	public static boolean propertyBoolean(String key, boolean defaultValue) {
		String value = configuration.get(key);
		if (value == null || value.isBlank()) return defaultValue;
		return parseBoolean(value);
	}

	/// Binds to the value of the provided configuration key as a [Boolean].
	///
	/// The provided [Consumer] will be run first with the initial value, as returned by
	/// [#propertyBoolean(String)], and it will be re-run for any configuration reloads.
	///
	/// @since 1.0
	public static void propertyBooleanBinding(String key, Consumer<Boolean> binding) {
		binding.accept(propertyBoolean(key));
		bindings.add(new ConfigurationBinding<>(key, Boolean::parseBoolean, binding));
	}
	//#endregion

	//#region int properties
	/// Returns the value of the provided configuration key as an `int`.
	/// @throws IllegalStateException if the configuration key is not set
	/// @throws NumberFormatException failed to parse the set value
	/// @since 1.0
	public static int propertyInteger(String key) {
		String value = configuration.get(key);
		if (value == null || value.isBlank()) throw new IllegalStateException();
		return parseInt(value);
	}

	/// Returns the value of the provided configuration key as an `int`
	/// if available, otherwise returning the provided fallback value.
	/// @throws NumberFormatException failed to parse the set value
	/// @since 1.0
	public static int propertyInteger(String key, int defaultValue) {
		String value = configuration.get(key);
		if (value == null || value.isBlank()) return defaultValue;
		return parseInt(value);
	}

	/// Binds to the value of the provided configuration key as an [Integer].
	///
	/// The provided [Consumer] will be run first with the initial value, as returned by
	/// [#propertyInteger(String)], and it will be re-run for any configuration reloads.
	///
	/// @since 1.0
	public static void propertyIntegerBinding(String key, Consumer<Integer> binding) {
		binding.accept(propertyInteger(key));
		bindings.add(new ConfigurationBinding<>(key, Integer::parseInt, binding));
	}
	//#endregion

	//#region long properties
	/// Returns the value of the provided configuration key as a `long`.
	/// @throws IllegalStateException if the configuration key is not set
	/// @throws NumberFormatException failed to parse the set value
	/// @since 1.0
	public static long propertyLong(String key) {
		String value = configuration.get(key);
		if (value == null || value.isBlank()) throw new IllegalStateException();
		return parseLong(value);
	}

	/// Returns the value of the provided configuration key as a `long`
	/// if available, otherwise returning the provided fallback value.
	/// @throws NumberFormatException failed to parse the set value
	/// @since 1.0
	public static long propertyLong(String key, long defaultValue) {
		String value = configuration.get(key);
		if (value == null || value.isBlank()) return defaultValue;
		return parseLong(value);
	}

	/// Binds to the value of the provided configuration key as a [Long].
	///
	/// The provided [Consumer] will be run first with the initial value, as returned by
	/// [#propertyLong(String)], and it will be re-run for any configuration reloads.
	///
	/// @since 1.0
	public static void propertyLongBinding(String key, Consumer<Long> binding) {
		binding.accept(propertyLong(key));
		bindings.add(new ConfigurationBinding<>(key, Long::parseLong, binding));
	}
	//#endregion

	//#region mapped properties
	/// Returns the value of the provided configuration key as `T` based on the
	/// provided mapping function that should return `T` from a [String] input.
	///
	/// For example, `propertyAs("EXAMPLE_BIGINTEGER", BigInteger::parseInt)`.
	/// @throws IllegalStateException if the configuration key is not set
	/// @since 1.0
	public static <T>
	T propertyAs(String key, Function<String, T> mapper) {
		String value = configuration.get(key);
		if (value == null || value.isBlank()) throw new IllegalStateException();
		return mapper.apply(value);
	}

	/// Binds to the value of the provided configuration key, using the provided
	/// mapping function, just like [#propertyAs].
	///
	/// The provided [Consumer] will be run first with the initial value, as returned by
	/// [#propertyAs], and it will be re-run for any configuration reloads.
	///
	/// @since 1.0
	public static <T>
	void propertyBindingAs(String key, Function<String, T> mapper, Consumer<T> binding) {
		binding.accept(propertyAs(key, mapper));
		bindings.add(new ConfigurationBinding<>(key, mapper, binding));
	}
	//#endregion
}