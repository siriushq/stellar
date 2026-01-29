package sirius.stellar.logging.dispatch.log4j2;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.logging.Logger.collector;

final class Log4j2DispatcherTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));

        var log4j2 = org.apache.logging.log4j.LogManager.getLogger(Log4j2DispatcherTest.class);
        log4j2.info("Hello from Log4J2!");

		assertThat(received).isTrue();
	}
}