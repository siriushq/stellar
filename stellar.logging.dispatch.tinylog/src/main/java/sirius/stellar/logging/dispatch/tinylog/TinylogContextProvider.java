package sirius.stellar.logging.dispatch.tinylog;

import sirius.stellar.facility.annotation.Internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// Implementation of [org.tinylog.provider.ContextProvider] used by [TinylogDispatcher].
@Internal
final class TinylogContextProvider implements org.tinylog.provider.ContextProvider {

	private final Map<String, String> map;

	TinylogContextProvider() {
		this.map = new ConcurrentHashMap<>();
	}

	@Override
	public Map<String, String> getMapping() {
		return this.map;
	}

	@Override
	public String get(String key) {
		return this.map.get(key);
	}

	@Override
	public void put(String key, Object value) {
		if (key == null || key.isEmpty() || key.isBlank()) throw new NullPointerException("Key must not be null, empty or blank");
		this.map.put(key, String.valueOf(value));
	}

	@Override
	public void remove(String key) {
		this.map.remove(key);
	}

	@Override
	public void clear() {
		this.map.clear();
	}
}