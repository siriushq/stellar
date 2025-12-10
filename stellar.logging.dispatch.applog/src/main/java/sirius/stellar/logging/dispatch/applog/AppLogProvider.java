package sirius.stellar.logging.dispatch.applog;

import sirius.stellar.logging.dispatch.jsr379x.Jsr379Dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/// Implementation of [io.avaje.applog.AppLog.Provider] used for obtaining instances of [Jsr379Dispatcher].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class AppLogProvider implements io.avaje.applog.AppLog.Provider {

	private static final Map<String, Jsr379Dispatcher> loggers = new HashMap<>();

	@Override
	public System.Logger getLogger(String name) {
		return loggers.computeIfAbsent(name, Jsr379Dispatcher::new);
	}

	@Override
	public System.Logger getLogger(String name, ResourceBundle bundle) {
		return loggers.computeIfAbsent(name, __ -> new Jsr379Dispatcher(name, bundle));
	}
}