package sirius.stellar.logging.dispatch.kwik;

import org.jspecify.annotations.Nullable;
import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static sirius.stellar.facility.Throwables.*;

/// Implementation of [tech.kwik.core.log.Logger] which delegates to [Logger].
/// This should be instantiated manually for use and provided to these methods or similar:
///
/// - [tech.kwik.core.QuicClientConnection.Builder#logger(tech.kwik.core.log.Logger)]
/// - [tech.kwik.core.server.ServerConnector.Builder#withLogger(tech.kwik.core.log.Logger)]
///
/// @since 1.0
/// @author Mechite
public final class KwikDispatcher extends tech.kwik.core.log.BaseLogger {

	private final Lock lock;

	public KwikDispatcher() {
		this.lock = new ReentrantLock();
	}

	@Override
	protected void log(@Nullable String text) {
		try {
			this.lock.lock();
			Logger.dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), "tech.kwik", text);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	protected void log(@Nullable String text, @Nullable Throwable throwable) {
		try {
			this.lock.lock();
			if (throwable != null) text += "\n" + stacktrace(throwable);
			Logger.dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), "tech.kwik", text);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	protected void logWithHexDump(@Nullable String text, byte[] data, int length) {
		try {
			this.lock.lock();
			text += "\n" + this.byteToHexBlock(data, length);

			Logger.dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), "tech.kwik", text);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	protected void logWithHexDump(@Nullable String text, @Nullable ByteBuffer data, int offset, int length) {
		try {
			this.lock.lock();
			text += "\n" + this.byteToHexBlock(data, offset, length);

			Logger.dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), "tech.kwik", text);
		} finally {
			this.lock.unlock();
		}
	}
}