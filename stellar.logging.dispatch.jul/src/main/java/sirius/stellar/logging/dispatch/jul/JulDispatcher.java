package sirius.stellar.logging.dispatch.jul;

import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;
import sirius.stellar.logging.spi.LoggerDispatcher;

import java.util.Map;

import static java.lang.Thread.currentThread;
import static java.text.MessageFormat.format;

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
public final class JulDispatcher
		extends java.util.logging.Handler
		implements LoggerDispatcher {

	private static final java.util.logging.LogManager
		manager = java.util.logging.LogManager.getLogManager();

	private static final Map<java.util.logging.Level, LoggerLevel>
		conversions = Map.of(
			java.util.logging.Level.FINEST, LoggerLevel.TRACING,
			java.util.logging.Level.FINER, LoggerLevel.DIAGNOSIS,
			java.util.logging.Level.FINE, LoggerLevel.DIAGNOSIS,
			java.util.logging.Level.CONFIG, LoggerLevel.CONFIGURATION,
			java.util.logging.Level.INFO, LoggerLevel.INFORMATION,
			java.util.logging.Level.WARNING, LoggerLevel.WARNING,
			java.util.logging.Level.SEVERE, LoggerLevel.ERROR
		);

	@Override
	public void wire() {
		java.util.logging.Logger root = manager.getLogger("");
		root.addHandler(this);
	}

	@Override
	public void publish(java.util.logging.LogRecord record) {
		java.util.logging.Level original = record.getLevel();
		if (original == null) return;

		LoggerLevel level = conversions.get(original);
		if (level == null) return;

		message()
			.level(level)
			.time(record.getInstant())
			.thread(currentThread().getName())
			.name(record.getSourceClassName())
			.text(format(record.getMessage(), record.getParameters()))
			.dispatch();
	}

	@Override
	public void flush() {
		assert true;
	}

	@Override
	public void close() {
		assert true;
	}
}