package sirius.stellar.logging.dispatch.tinylog;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.locks.LockSupport.parkNanos;
import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.logging.Logger.collector;

final class TinylogDispatcherTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));

        org.tinylog.Logger.info("Hello from tinylog!");

		for (int seconds = 0;
			 seconds < 10 || !received.get();
			 seconds++) parkNanos(100L);

		assertThat(received).isTrue();
	}
}