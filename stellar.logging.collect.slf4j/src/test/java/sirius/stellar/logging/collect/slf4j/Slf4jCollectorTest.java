package sirius.stellar.logging.collect.slf4j;

import com.github.valfirst.slf4jtest.TestLoggerFactory;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.event.Level.INFO;
import static sirius.stellar.logging.Logger.*;
import static sirius.stellar.logging.LoggerLevel.INFORMATION;

final class Slf4jCollectorTest {

	@Test
	void log() {
        var slf4j = TestLoggerFactory.getTestLogger(Slf4jCollectorTest.class);

		collector(message -> {
			var text = message.text();
			var level = message.level();

			assertThat(text).isEqualTo("Hello using SLF4j collector!");
			assertThat(level).isEqualTo(INFORMATION);
		});

		information("Hello using SLF4j collector!");

		var events = slf4j.getAllLoggingEvents();
		assertThat(events).anySatisfy(event -> {
			var message = event.getMessage();
			var level = event.getLevel();
			var arguments = event.getArguments();
			var throwable = event.getThrowable();
			var mdc = event.getMdc();
			var markers = event.getMarkers();
			var pairs = event.getKeyValuePairs();

			assertThat(message).isEqualTo("Hello using SLF4j collector!");
			assertThat(level).isEqualTo(INFO);
			assertThat(arguments).isEmpty();
			assertThat(throwable).isEqualTo(Optional.empty());
			assertThat(mdc).isEmpty();
			assertThat(markers).isEmpty();
			assertThat(pairs).isEmpty();
		});
	}
}