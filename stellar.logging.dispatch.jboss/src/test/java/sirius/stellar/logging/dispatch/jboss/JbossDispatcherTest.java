package sirius.stellar.logging.dispatch.jboss;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.*;
import static sirius.stellar.logging.Logger.*;

final class JbossDispatcherTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));
		synchronous();

        var jboss = org.jboss.logging.Logger.getLogger(JbossDispatcherTest.class);
        jboss.info("Hello from JBoss!");

		assertThat(received).isTrue();
	}
}