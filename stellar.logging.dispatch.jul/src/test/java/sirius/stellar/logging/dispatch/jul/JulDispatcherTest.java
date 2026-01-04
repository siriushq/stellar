package sirius.stellar.logging.dispatch.jul;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.logging.Logger.collector;
import static sirius.stellar.logging.Logger.synchronous;

final class JulDispatcherTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));
		synchronous();

        var jul = java.util.logging.Logger.getLogger(this.getClass().getCanonicalName());
        jul.info("Hello from java.util.logging!");

		assertThat(received).isTrue();
	}
}