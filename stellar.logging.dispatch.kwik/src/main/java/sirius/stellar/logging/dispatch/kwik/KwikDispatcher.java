package sirius.stellar.logging.dispatch.kwik;

import org.jspecify.annotations.Nullable;
import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerMessage;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.currentThread;
import static sirius.stellar.logging.LoggerLevel.INFORMATION;

/// Implementation of [tech.kwik.core.log.Logger] which delegates to [Logger].
/// This should be instantiated manually for use and provided to these methods or similar:
///
/// - [tech.kwik.core.QuicClientConnection.Builder#logger(tech.kwik.core.log.Logger)]
/// - [tech.kwik.core.server.ServerConnector.Builder#withLogger(tech.kwik.core.log.Logger)]
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class KwikDispatcher extends tech.kwik.core.log.BaseLogger {

	private final Lock lock;

	public KwikDispatcher() {
		this.lock = new ReentrantLock();
	}

	@Override
	protected void log(@Nullable String text) {
		try {
			this.lock.lock();
			LoggerMessage.builder()
					.level(INFORMATION)
					.time(Instant.now())
					.thread(currentThread().getName())
					.name("tech.kwik")
					.text(String.valueOf(text))
					.dispatch();
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	protected void log(@Nullable String text, @Nullable Throwable throwable) {
		try {
			this.lock.lock();
			LoggerMessage.builder()
					.level(INFORMATION)
					.time(Instant.now())
					.thread(currentThread().getName())
					.name("tech.kwik")
					.text(String.valueOf(text))
					.throwable(throwable)
					.dispatch();
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	protected void logWithHexDump(@Nullable String text, byte[] data, int length) {
		try {
			this.lock.lock();
			if (text == null) text = "";
			text += "\n" + this.byteToHexBlock(data, length);

			LoggerMessage.builder()
					.level(INFORMATION)
					.time(Instant.now())
					.thread(currentThread().getName())
					.name("tech.kwik")
					.text(text)
					.dispatch();
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	protected void logWithHexDump(@Nullable String text, @Nullable ByteBuffer data, int offset, int length) {
		try {
			this.lock.lock();
			if (text == null) text = "";
			text += "\n" + this.byteToHexBlock(data, offset, length);

			LoggerMessage.builder()
					.level(INFORMATION)
					.time(Instant.now())
					.thread(currentThread().getName())
					.name("tech.kwik")
					.text(text)
					.dispatch();
		} finally {
			this.lock.unlock();
		}
	}
}