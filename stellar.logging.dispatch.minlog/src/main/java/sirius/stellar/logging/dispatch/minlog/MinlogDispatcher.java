package sirius.stellar.logging.dispatch.minlog;

import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;
import sirius.stellar.logging.LoggerMessage;
import sirius.stellar.logging.dispatch.Dispatcher;

import java.time.Instant;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;
import static java.lang.Thread.currentThread;

/// Implementation of [com.esotericsoftware.minlog.Log.Logger] which dispatches to [Logger].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class MinlogDispatcher
		extends com.esotericsoftware.minlog.Log.Logger
		implements Dispatcher {

	private static final StackWalker walker = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);

	@Override
	public void wire() {
		com.esotericsoftware.minlog.Log.setLogger(this);
	}

	@Override
	public void log(int level, String category, String text, Throwable throwable) {
		LoggerLevel converted = switch (level) {
			case 1 -> LoggerLevel.TRACING;
			case 2 -> LoggerLevel.DIAGNOSIS;
			case 3 -> LoggerLevel.INFORMATION;
			case 4 -> LoggerLevel.WARNING;
			case 5 -> LoggerLevel.ERROR;
			default -> null;
		};
		if (converted == null) return;
		if (!Logger.enabled(converted)) return;

		String caller = walker.walk(stream -> stream.limit(3)
				.toList())
				.get(2)
				.getClassName();
		LoggerMessage.builder()
				.level(converted)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name((caller != null) ? caller : "com.esotericsoftware.minlog")
				.text(String.valueOf(text))
				.throwable(throwable)
				.dispatch();
	}
}