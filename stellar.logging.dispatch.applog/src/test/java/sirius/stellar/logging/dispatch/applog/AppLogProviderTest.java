package sirius.stellar.logging.dispatch.applog;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.*;
import static sirius.stellar.logging.Logger.*;

final class AppLogProviderTest {

	@Test
	void log() {
		var received = new AtomicBoolean(false);
		collector(message -> received.set(true));

        var jsr379 = io.avaje.applog.AppLog.getLogger(this.getClass().getCanonicalName());
        jsr379.log(System.Logger.Level.INFO, "Hello from System.Logger!");

		assertThat(received).isTrue();
	}
}