package sirius.stellar.logging.dispatch.jboss;

import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;
import sirius.stellar.logging.LoggerMessage;

import java.text.MessageFormat;
import java.time.Instant;

import static java.lang.Thread.currentThread;

/// Implementation of [org.jboss.logging.Logger] which delegates to [Logger].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class JbossDispatcher extends org.jboss.logging.Logger {

	JbossDispatcher(String name) {
		super(name);
	}

	@Override
	protected void doLog(org.jboss.logging.Logger.Level level, String name, Object object, Object[] arguments, Throwable throwable) {
		if (!isEnabled(level)) return;
		String text = String.valueOf(object);
		LoggerMessage.builder()
				.level(convert(level))
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.getName())
				.text(MessageFormat.format(text, arguments))
				.throwable(throwable)
				.dispatch();
	}

	@Override
	protected void doLogf(org.jboss.logging.Logger.Level level, String name, String text, Object[] arguments, Throwable throwable) {
		if (!isEnabled(level)) return;
		LoggerMessage.builder()
				.level(convert(level))
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.getName())
				.text(String.format(String.valueOf(text), arguments))
				.throwable(throwable)
				.dispatch();
	}

	@Override
	public boolean isEnabled(org.jboss.logging.Logger.Level level) {
		return Logger.enabled(convert(level));
	}

	/// Converts the provided level to a [LoggerLevel].
	private LoggerLevel convert(org.jboss.logging.Logger.Level level) {
		return switch (level) {
			case FATAL, ERROR -> LoggerLevel.ERROR;
			case WARN -> LoggerLevel.WARNING;
        	case INFO -> LoggerLevel.INFORMATION;
        	case DEBUG -> LoggerLevel.DIAGNOSIS;
			case TRACE -> LoggerLevel.TRACING;
		};
	}
}