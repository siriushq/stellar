package sirius.stellar.configuration.testing;

import sirius.stellar.configuration.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// Shared static state for applications and libraries which are mutating
/// [Configuration] in tests. This should never be used in non-testing code.
///
/// @since 1.0
public final class TestConfigurationState {

	/// Key-value pairs that are to be served by [TestConfigurationProvider].
	static Map<String, String> configuration = new ConcurrentHashMap<>();

	/// Put ([Map#put]) a key-value pair programmatically.
	/// This should never be used in non-testing code.
	///
	/// @since 1.0
	public static void put(String key, String value) {
		configuration.put(key, value);
	}
}