package sirius.stellar.configuration.javaprefs;

import io.avaje.spi.ServiceProvider;
import org.jspecify.annotations.Nullable;
import sirius.stellar.configuration.Configuration;
import sirius.stellar.configuration.ConfigurationProvider;
import sirius.stellar.configuration.ConfigurationReloader;

import java.time.Year;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.Preferences;

import static java.lang.System.err;
import static java.lang.System.out;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

/// Configuration provider and reloader for the `java.util.prefs` API.
/// @see Configuration
@ServiceProvider
public final class JavaprefsConfiguration
	implements ConfigurationProvider, ConfigurationReloader {

	private final Map<String, String> map;

	@Nullable
	private Preferences node;

	public JavaprefsConfiguration() {
		this.map = new ConcurrentHashMap<>();

		try {
			Preferences root = requireNonNull(Preferences.userRoot());
			this.node = root.node("{sirius.stellar.configuration}");
		} catch (Throwable ignored) {
			err.println("Failure registering java.util.prefs configuration provider, ignoring");
		}
	}

	/// Entry-point when this module is invoked as an application directly.
	///
	/// This provides a utility to read and set configuration keys/values in the
	/// [Preferences] node that is used (can be bound to, causing reloads).
	public static void main(String[] arguments) throws BackingStoreException {
		JavaprefsConfiguration self = new JavaprefsConfiguration();
		if (self.node == null) throw new IllegalStateException();

		switch (arguments.length) {
		case 0:
			out.println(self.get()
				.entrySet()
				.stream()
				.map(it -> it.getKey() + "=" + it.getValue())
				.collect(joining("\n")));
			break;
		case 1:
			String key = arguments[0];
			String value = self.node.get(key, "");
			out.println(key + "=" + value);
			break;
		case 2:
			self.node.put(arguments[0], arguments[1]);
			break;
		default:
			err.println("Usage: [key] [value]");
			break;
		}
	}

	@Override
	public Map<String, String> get() throws BackingStoreException {
		if (this.node == null || !this.map.isEmpty()) return unmodifiableMap(this.map);

		for (String key : this.node.keys()) {
			String value = this.node.get(key, "");
			this.map.put(key, value);
		}
		return unmodifiableMap(this.map);
	}

	@Override
	public void wire() {
		if (this.node == null) return;
		this.node.addPreferenceChangeListener(this::listen);
	}

	/// Receives all preference change events, filtering for only preferences
	/// that were returned upon the first invocation of [#get()].
	private void listen(PreferenceChangeEvent event) {
		String key = event.getKey();
		String value = event.getNewValue();

		if (value == null) {
			this.map.remove(key);
			return;
		}
		if (!this.map.containsKey(key)) return;
		this.map.put(key, value);
		this.reload();
	}
}