package sirius.stellar.logging.dispatch.tinylog;

import org.tinylog.format.AdvancedMessageFormatter;
import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;
import sirius.stellar.logging.LoggerMessage;

import java.time.Instant;
import java.util.Locale;

import static java.lang.Thread.currentThread;

/// Implementation of [org.tinylog.provider.LoggingProvider] which dispatches to [Logger].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class TinylogDispatcher
		implements org.tinylog.provider.LoggingProvider {

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

		int limit = (depth + 1);
		String caller = walker.walk(stream -> stream.limit(limit)
				.toList())
				.get(depth)
				.getClassName();
		this.log(caller, tag, level, throwable, formatter, o, objects);
	}

	@Override
	public void log(String caller, String tag, org.tinylog.Level level, Throwable throwable, org.tinylog.format.MessageFormatter formatter, Object o, Object... objects) {
		if (level == null) return;
		if (!Logger.enabled(this.convert(level))) return;
		if (formatter == null) formatter = new AdvancedMessageFormatter(Locale.getDefault(), true);

		String text = String.valueOf(o);
		if (objects != null) text = formatter.format(text, objects);

		if (tag != null && !tag.isBlank()) text = "[" + tag + "] " + text;

		LoggerMessage.builder()
				.level(this.convert(level))
				.time(Instant.now())
				.thread(currentThread().getName())
				.name((caller != null) ? caller : "org.tinylog")
				.text(text)
				.throwable(throwable)
				.dispatch();
	}

	/// Converts the provided level to a [LoggerLevel].
	private LoggerLevel convert(org.tinylog.Level level) {
		return switch (level) {
			case INFO -> LoggerLevel.INFORMATION;
			case WARN -> LoggerLevel.WARNING;
			case ERROR -> LoggerLevel.ERROR;
			case TRACE -> LoggerLevel.TRACING;
			case DEBUG -> LoggerLevel.DIAGNOSIS;
			default -> LoggerLevel.OFF;
		};
	}

	@Override
	public void shutdown() {
		assert true;
	}
}