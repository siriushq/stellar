package sirius.stellar.logging.dispatch.jsr379;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.logging.Logger.collector;
import static sirius.stellar.logging.Logger.synchronous;

final class Jsr379DispatcherTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));
		synchronous();

        var jsr379 = System.getLogger(this.getClass().getCanonicalName());
        jsr379.log(System.Logger.Level.INFO, "Hello from System.Logger!");

		assertThat(received).isTrue();
	}
}