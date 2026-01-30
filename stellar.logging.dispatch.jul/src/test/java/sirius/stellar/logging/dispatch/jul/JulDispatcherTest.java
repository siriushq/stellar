package sirius.stellar.logging.dispatch.jul;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.onSpinWait;
import static java.util.concurrent.locks.LockSupport.parkNanos;
import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.logging.Logger.collector;

@Timeout(5)
final class JulDispatcherTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));

        var jul = java.util.logging.Logger.getLogger(this.getClass().getCanonicalName());
        jul.info("Hello from java.util.logging!");

		while (!received.get()) onSpinWait();
		assertThat(received).isTrue();
	}
}