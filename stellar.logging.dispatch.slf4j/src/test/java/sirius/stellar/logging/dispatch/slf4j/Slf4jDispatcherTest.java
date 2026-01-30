package sirius.stellar.logging.dispatch.slf4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.onSpinWait;
import static java.util.concurrent.locks.LockSupport.parkNanos;
import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.logging.Logger.collector;

@Timeout(5)
final class Slf4jDispatcherTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));

        var slf4j = org.slf4j.LoggerFactory.getLogger(Slf4jDispatcherTest.class);
        slf4j.info("Hello from SLF4j!");

		while (!received.get()) onSpinWait();
		assertThat(received).isTrue();
	}
}