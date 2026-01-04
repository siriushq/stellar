package sirius.stellar.logging.dispatch.log4j;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.logging.Logger.collector;
import static sirius.stellar.logging.Logger.synchronous;

final class Log4jDispatcherTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));
		synchronous();

        var log4j = org.apache.log4j.Logger.getLogger(Log4jDispatcherTest.class);
        log4j.info("Hello from Log4J!");

		assertThat(received).isTrue();
	}
}