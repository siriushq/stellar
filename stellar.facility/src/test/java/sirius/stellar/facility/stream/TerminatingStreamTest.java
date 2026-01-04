package sirius.stellar.facility.stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static sirius.stellar.facility.stream.TerminatingStream.terminalStream;

final class TerminatingStreamTest {

	@Test @DisplayName("Stream wrapped with #terminalStream(...) correctly terminates provided stream on terminal operation")
	void terminalStreamTerminatesProvidedStreamOnTerminalOperation() {
		var stream = Stream.of("a", "b", "c", "d");
		var runnable = mock(Runnable.class);
		stream = stream.onClose(runnable);

		var wrapped = terminalStream(stream);
		var values = wrapped.toList();

		verify(runnable, times(1)).run();
		assertThat(values).containsExactly("a", "b", "c", "d");
	}

	@Test @DisplayName("Stream wrapped with #terminalStream(...) handles intermediate operations correctly")
	void terminalStreamHandlesIntermediateOperations() {
		var stream = Stream.of("a", "b", "c", "d");
		var runnable = mock(Runnable.class);
		stream = stream.onClose(runnable);

		var values = terminalStream(stream)
				.filter(string -> !string.equals("c"))
				.map(String::toUpperCase)
				.toList();

		verify(runnable, times(1)).run();
		assertThat(values).containsExactly("A", "B", "D");
	}
}