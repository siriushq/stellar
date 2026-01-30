package sirius.stellar.logging.dispatch.jcl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.onSpinWait;
import static java.util.concurrent.locks.LockSupport.parkNanos;
import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.logging.Logger.collector;

@Timeout(5)
final class JclDispatcherTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));

        var jcl = org.apache.commons.logging.LogFactory.getLog(JclDispatcherTest.class);
        jcl.info("Hello from Jakarta/Apache Commons Logging!");

		while (!received.get()) onSpinWait();
		assertThat(received).isTrue();
	}
}