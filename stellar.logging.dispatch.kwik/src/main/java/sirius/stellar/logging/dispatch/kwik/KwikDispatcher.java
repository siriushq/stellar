package sirius.stellar.logging.dispatch.kwik;

import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static sirius.stellar.facility.Throwables.*;

/**
 * Implementation of {@link tech.kwik.core.log.Logger} which delegates to {@link Logger}.
 *
 * @since 1.0
 * @author Mechite
 */
public final class KwikDispatcher extends tech.kwik.core.log.BaseLogger {

	private final Lock lock;

	public KwikDispatcher() {
		this.lock = new ReentrantLock();
	}

	@Override
	protected void log(String text) {
		try {
			this.lock.lock();
			Logger.dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), "tech.kwik", text);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	protected void log(String text, Throwable throwable) {
		try {
			this.lock.lock();
			if (throwable != null) text += "\n" + stacktrace(throwable);
			Logger.dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), "tech.kwik", text);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	protected void logWithHexDump(String text, byte[] data, int length) {
		try {
			this.lock.lock();
			text += "\n" + this.byteToHexBlock(data, length);

			Logger.dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), "tech.kwik", text);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	protected void logWithHexDump(String text, ByteBuffer data, int offset, int length) {
		try {
			this.lock.lock();
			text += "\n" + this.byteToHexBlock(data, offset, length);

			Logger.dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), "tech.kwik", text);
		} finally {
			this.lock.unlock();
		}
	}
}