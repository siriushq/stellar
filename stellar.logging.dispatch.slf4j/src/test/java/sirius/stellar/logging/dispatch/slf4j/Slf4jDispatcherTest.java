package sirius.stellar.logging.dispatch.slf4j;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.logging.Logger.collector;
import static sirius.stellar.logging.Logger.synchronous;

final class Slf4jDispatcherTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));
		synchronous();

        var slf4j = org.slf4j.LoggerFactory.getLogger(Slf4jDispatcherTest.class);
        slf4j.info("Hello from SLF4j!");

		assertThat(received).isTrue();
	}
}