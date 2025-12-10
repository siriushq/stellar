package sirius.stellar.logging.collect.slf4j;

import com.github.valfirst.slf4jtest.TestLoggerFactory;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.slf4j.event.Level.*;
import static sirius.stellar.logging.Logger.*;
import static sirius.stellar.logging.LoggerLevel.*;

final class Slf4jCollectorTest {

	@Test
	void log() {
        var slf4j = TestLoggerFactory.getTestLogger(Slf4jCollectorTest.class);

		synchronous();
		collector(message -> {
			assertThat(message.text()).isEqualTo("Hello using SLF4j collector!");
			assertThat(message.level()).isEqualTo(INFORMATION);
		});

		information("Hello using SLF4j collector!");

		assertThat(slf4j.getAllLoggingEvents()).anySatisfy(event -> {
			assertThat(event.getMessage()).isEqualTo("Hello using SLF4j collector!");
			assertThat(event.getLevel()).isEqualTo(INFO);
			assertThat(event.getArguments()).isEmpty();
			assertThat(event.getThrowable()).isEqualTo(Optional.empty());
			assertThat(event.getMdc()).isEmpty();
			assertThat(event.getMarkers()).isEmpty();
			assertThat(event.getKeyValuePairs()).isEmpty();
		});
	}
}