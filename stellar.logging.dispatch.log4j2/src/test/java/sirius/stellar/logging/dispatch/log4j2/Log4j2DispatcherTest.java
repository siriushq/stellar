package sirius.stellar.logging.dispatch.log4j2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.onSpinWait;
import static java.util.concurrent.locks.LockSupport.parkNanos;
import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.logging.Logger.collector;

@Timeout(5)
final class Log4j2DispatcherTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));

        var log4j2 = org.apache.logging.log4j.LogManager.getLogger(Log4j2DispatcherTest.class);
        log4j2.info("Hello from Log4J2!");

		while (!received.get()) onSpinWait();
		assertThat(received).isTrue();
	}
}