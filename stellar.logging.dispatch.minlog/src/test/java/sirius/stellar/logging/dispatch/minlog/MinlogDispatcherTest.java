package sirius.stellar.logging.dispatch.minlog;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.logging.Logger.collector;
import static sirius.stellar.logging.Logger.synchronous;

final class MinlogDispatcherTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));
		synchronous();

        com.esotericsoftware.minlog.Log.info("Hello from minlog!");

		assertThat(received).isTrue();
	}
}