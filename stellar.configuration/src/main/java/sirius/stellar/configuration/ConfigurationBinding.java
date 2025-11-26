package sirius.stellar.configuration;

import java.util.function.Consumer;
import java.util.function.Function;

/// Represents a configuration reload binding, which is registered by
/// methods such as [Configuration#propertyBinding].
final class ConfigurationBinding<T> {

	private final String key;
	private final Function<String,T> mapper;
	private final Consumer<T> binding;

	ConfigurationBinding(String key, Function<String, T> mapper, Consumer<T> binding) {
		this.key = key;
		this.mapper = mapper;
		this.binding = binding;
	}

	/// The key this binding is watching.
	String key() {
		return this.key;
	}

	/// Issue an update to this binding with the new provided value.
	void update(String value) {
		try {
			T t = this.mapper.apply(value);
			this.binding.accept(t);
		} catch (Throwable throwable) {
			throw new IllegalStateException("Failed to parse '" + this.key + "' in configuration", throwable);
		}
	}
}