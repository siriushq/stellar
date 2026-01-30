package sirius.stellar.logging.dispatch.applog;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.locks.LockSupport.parkNanos;
import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.logging.Logger.collector;

final class AppLogProviderTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));

        var jsr379 = io.avaje.applog.AppLog.getLogger(this.getClass().getCanonicalName());
        jsr379.log(System.Logger.Level.INFO, "Hello from System.Logger!");

		for (int seconds = 0;
			 seconds < 10 || !received.get();
			 seconds++) parkNanos(100L);

		assertThat(received).isTrue();
	}
}