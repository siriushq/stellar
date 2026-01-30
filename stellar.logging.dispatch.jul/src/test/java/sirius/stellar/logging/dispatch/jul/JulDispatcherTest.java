package sirius.stellar.logging.dispatch.jul;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.locks.LockSupport.parkNanos;
import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.logging.Logger.collector;

final class JulDispatcherTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));

        var jul = java.util.logging.Logger.getLogger(this.getClass().getCanonicalName());
        jul.info("Hello from java.util.logging!");

		for (int seconds = 0;
			 seconds < 10 || !received.get();
			 seconds++) parkNanos(100L);

		assertThat(received).isTrue();
	}
}