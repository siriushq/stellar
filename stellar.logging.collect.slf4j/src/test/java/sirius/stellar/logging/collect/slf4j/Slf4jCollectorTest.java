package sirius.stellar.logging.collect.slf4j;

import com.github.valfirst.slf4jtest.TestLoggerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.onSpinWait;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.event.Level.INFO;
import static sirius.stellar.logging.Logger.collector;
import static sirius.stellar.logging.Logger.information;
import static sirius.stellar.logging.LoggerLevel.INFORMATION;

@Timeout(5)
final class Slf4jCollectorTest {

	@Test
	void log() {
        var slf4j = TestLoggerFactory.getTestLogger(Slf4jCollectorTest.class);
		var received = new AtomicBoolean(false);
		var text = "Hello using SLF4j collector!";

		collector(message -> {
			received.set(true);
			assertThat(message.text())
				.isEqualTo(text);
			assertThat(message.level())
				.isEqualTo(INFORMATION);
		});

		information(text);
		while (!received.get()) onSpinWait();

		assertThat(slf4j.getAllLoggingEvents())
			.isNotEmpty()
			.anySatisfy(event -> {
				assertThat(event.getMessage())
					.isEqualTo(text);
				assertThat(event.getLevel())
					.isEqualTo(INFO);
				assertThat(event.getArguments())
					.isEmpty();
				assertThat(event.getThrowable())
					.isEqualTo(Optional.empty());
				assertThat(event.getMdc())
					.isEmpty();
				assertThat(event.getMarkers())
					.isEmpty();
				assertThat(event.getKeyValuePairs())
					.isEmpty();
			});
	}
}