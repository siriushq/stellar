package sirius.stellar.logging.dispatch.jsr379;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.locks.LockSupport.parkNanos;
import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.logging.Logger.collector;

final class Jsr379DispatcherTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));

        var jsr379 = System.getLogger(this.getClass().getCanonicalName());
        jsr379.log(System.Logger.Level.INFO, "Hello from System.Logger!");

		for (int seconds = 0;
			 seconds < 10 || !received.get();
			 seconds++) parkNanos(100L);

		assertThat(received).isTrue();
	}
}