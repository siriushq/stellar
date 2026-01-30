package sirius.stellar.logging.dispatch.applog;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.onSpinWait;
import static java.util.concurrent.locks.LockSupport.parkNanos;
import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.logging.Logger.collector;

@Timeout(5)
final class AppLogProviderTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));

        var jsr379 = io.avaje.applog.AppLog.getLogger(this.getClass().getCanonicalName());
        jsr379.log(System.Logger.Level.INFO, "Hello from System.Logger!");

		while (!received.get()) onSpinWait();
		assertThat(received).isTrue();
	}
}