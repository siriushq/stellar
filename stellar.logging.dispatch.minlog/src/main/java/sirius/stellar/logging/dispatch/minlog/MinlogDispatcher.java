package sirius.stellar.logging.dispatch.minlog;

import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;
import sirius.stellar.logging.dispatch.Dispatcher;

import java.io.ObjectStreamException;
import java.io.Serial;
import java.time.Instant;

import static sirius.stellar.facility.Throwables.*;

/// Implementation of [com.esotericsoftware.minlog.Log.Logger] which dispatches to [Logger].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class MinlogDispatcher extends com.esotericsoftware.minlog.Log.Logger implements Dispatcher {

	@Serial
	private static final long serialVersionUID = 2065320453211284579L;

	private static final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

	private transient final Provider provider;

	MinlogDispatcher(Dispatcher.Provider provider) {
		this.provider = provider;
	}

	@Override
	public void wire() {
		com.esotericsoftware.minlog.Log.setLogger(this);
	}

	@Override
	public void log(int level, String category, String message, Throwable throwable) {
		LoggerLevel loggerLevel = switch (level) {
			case 1 -> LoggerLevel.STACKTRACE;
			case 2 -> LoggerLevel.DEBUGGING;
			case 3 -> LoggerLevel.INFORMATION;
			case 4 -> LoggerLevel.WARNING;
			case 5 -> LoggerLevel.ERROR;
			default -> null;
		};
		if (loggerLevel == null) return;
		if (!Logger.enabled(loggerLevel)) return;

		if (throwable != null) message += "\n" + stacktrace(throwable);
		String caller = String.valueOf(walker.walk(stream -> stream.limit(3).toList()).get(2).getClassName());
		Logger.dispatch(Instant.now(), loggerLevel, Thread.currentThread().getName(), caller, message);
	}

	@Serial
	private Object readResolve() throws ObjectStreamException {
		return this.provider.create();
	}
}