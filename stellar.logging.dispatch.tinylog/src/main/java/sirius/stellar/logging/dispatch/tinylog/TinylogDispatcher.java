package sirius.stellar.logging.dispatch.tinylog;

import org.tinylog.format.AdvancedMessageFormatter;
import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;

import java.time.Instant;
import java.util.Locale;

import static sirius.stellar.facility.Throwables.*;

/**
 * Implementation of {@link org.tinylog.provider.LoggingProvider} which dispatches to {@link Logger}.
 *
 * @since 1.0
 * @author Mechite
 */
public final class TinylogDispatcher implements org.tinylog.provider.LoggingProvider {

	private static final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

	@Override
	public org.tinylog.provider.ContextProvider getContextProvider() {
		return new TinylogContextProvider();
	}

	@Override
	public org.tinylog.Level getMinimumLevel() {
		return org.tinylog.Level.INFO;
	}

	@Override
	public org.tinylog.Level getMinimumLevel(String tag) {
		return org.tinylog.Level.INFO;
	}

	@Override
	public boolean isEnabled(int depth, String tag, org.tinylog.Level level) {
		LoggerLevel loggerLevel = this.convert(level);
		if (loggerLevel == null) return false;
		return Logger.enabled(loggerLevel);
	}

	@Override
	public void log(int depth, String tag, org.tinylog.Level level, Throwable throwable, org.tinylog.format.MessageFormatter formatter, Object o, Object... objects) {
		if (level == null) return;
		if (!isEnabled(depth, tag, level)) return;
		String caller = String.valueOf(walker.walk(stream -> stream.limit(depth + 1).toList()).get(depth).getClassName());
		this.log(caller, tag, level, throwable, formatter, o, objects);
	}

	@Override
	public void log(String caller, String tag, org.tinylog.Level level, Throwable throwable, org.tinylog.format.MessageFormatter formatter, Object o, Object... objects) {
		if (level == null) return;
		if (!Logger.enabled(this.convert(level))) return;
		if (formatter == null) formatter = new AdvancedMessageFormatter(Locale.getDefault(), true);

		String message = String.valueOf(o);
		if (objects != null) message = formatter.format(message, objects);

		if (tag != null && !tag.isEmpty() && !tag.isBlank()) message = "[" + tag + "] " + message;
		if (throwable != null) message += "\n" + stacktrace(throwable);
		Logger.dispatch(Instant.now(), this.convert(level), Thread.currentThread().getName(), caller, message);
	}

	/**
	 * Converts the provided level to a {@link LoggerLevel}.
	 */
	private LoggerLevel convert(org.tinylog.Level level) {
		return switch (level) {
			case INFO -> LoggerLevel.INFORMATION;
			case WARN -> LoggerLevel.WARNING;
			case ERROR -> LoggerLevel.ERROR;
			case TRACE -> LoggerLevel.STACKTRACE;
			case DEBUG -> LoggerLevel.DEBUGGING;
			default -> null;
		};
	}

	@Override
	public void shutdown() {
		assert true;
	}
}