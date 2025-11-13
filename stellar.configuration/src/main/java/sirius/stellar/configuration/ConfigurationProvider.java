package sirius.stellar.configuration;

import sirius.stellar.lifecycle.spi.Service;

import java.util.List;
import java.util.Map;

import static java.util.Collections.*;

/// Represents a source of key-value configuration data to be loaded by [Configuration] on
/// the first attempt to access any configuration keys. These providers can declare preceding
/// providers that are to be loaded before itself.
@Service
public interface ConfigurationProvider {

	/// Obtain all keys/value configuration data.
	Map<String, String> get() throws Throwable;

	/// An array of providers that this one requires load before itself.
	default List<Class<? extends ConfigurationProvider>> preceding() {
		return emptyList();
	}
}