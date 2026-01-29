package sirius.stellar.logging.dispatch.jcl;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.logging.Logger.collector;

final class JclDispatcherTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));

        var jcl = org.apache.commons.logging.LogFactory.getLog(JclDispatcherTest.class);
        jcl.info("Hello from Jakarta/Apache Commons Logging!");

		assertThat(received).isTrue();
	}
}