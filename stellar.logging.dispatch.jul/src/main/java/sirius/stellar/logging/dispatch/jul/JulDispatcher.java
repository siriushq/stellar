package sirius.stellar.logging.dispatch.jul;

import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;
import sirius.stellar.logging.dispatch.Dispatcher;

import java.io.ObjectStreamException;
import java.io.Serial;
import java.text.MessageFormat;
import java.util.Map;

import static java.lang.Thread.*;

/// Implementation of [java.util.logging.Handler] which dispatches to [Logger].
///
/// -------------------------------
/// | `j.u.l`   | [LoggerLevel]   |
/// |-----------|-----------------|
/// | `FINEST`  | `STACKTRACE`    |
/// | `FINER`   | `DEBUGGING`     |
/// | `FINE`    | `DEBUGGING`     |
/// | `CONFIG`  | `CONFIGURATION` |
/// | `INFO`    | `INFORMATION`   |
/// | `WARNING` | `WARNING`       |
/// | `SEVERE`  | `ERROR`         |
/// -------------------------------
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class JulDispatcher extends java.util.logging.Handler implements Dispatcher {

	@Serial
	private static final long serialVersionUID = 2451156701961930648L;

	private static final java.util.logging.LogManager manager = java.util.logging.LogManager.getLogManager();
	private static final Map<java.util.logging.Level, LoggerLevel> conversions = Map.of(
			java.util.logging.Level.FINEST, LoggerLevel.STACKTRACE,
			java.util.logging.Level.FINER, LoggerLevel.DEBUGGING,
			java.util.logging.Level.FINE, LoggerLevel.DEBUGGING,
			java.util.logging.Level.CONFIG, LoggerLevel.CONFIGURATION,
			java.util.logging.Level.INFO, LoggerLevel.INFORMATION,
			java.util.logging.Level.WARNING, LoggerLevel.WARNING,
			java.util.logging.Level.SEVERE, LoggerLevel.ERROR
	);

	private transient final Dispatcher.Provider provider;

	JulDispatcher(Dispatcher.Provider provider) {
		this.provider = provider;
	}

	@Override
	public void wire() {
		manager.getLogger("").addHandler(this);
	}

	@Override
	public void publish(java.util.logging.LogRecord record) {
		java.util.logging.Level original = record.getLevel();
		if (original == null) return;
		LoggerLevel level = conversions.get(original);
		if (level == null) return;
		Logger.dispatch(record.getInstant(), level, currentThread().getName(), record.getSourceClassName(), MessageFormat.format(record.getMessage(), record.getParameters()));
	}

	@Override
	public void flush() {
		assert true;
	}

	@Override
	public void close() {
		assert true;
	}

	@Serial
	private Object readResolve() throws ObjectStreamException {
		return this.provider.create();
	}
}