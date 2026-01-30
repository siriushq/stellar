package sirius.stellar.logging.dispatch.log4j;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.locks.LockSupport.parkNanos;
import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.logging.Logger.collector;

final class Log4jDispatcherTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));

        var log4j = org.apache.log4j.Logger.getLogger(Log4jDispatcherTest.class);
        log4j.info("Hello from Log4J!");

		for (int seconds = 0;
			 seconds < 10 || !received.get();
			 seconds++) parkNanos(100L);

		assertThat(received).isTrue();
	}
}