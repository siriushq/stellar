package sirius.stellar.logging.dispatch.jsr379x;

import java.util.HashMap;
import java.util.Map;

/// Implementation of [System.LoggerFinder] used for obtaining instances of [Jsr379Dispatcher].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class Jsr379Finder extends System.LoggerFinder {

	private static final Map<String, Jsr379Dispatcher> loggers = new HashMap<>();

	@Override
	public System.Logger getLogger(String name, Module module) {
		return loggers.computeIfAbsent(name, Jsr379Dispatcher::new);
	}
}