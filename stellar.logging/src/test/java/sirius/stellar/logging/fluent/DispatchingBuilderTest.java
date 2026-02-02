package sirius.stellar.logging.fluent;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static sirius.stellar.logging.fluent.DispatchingBuilder.traceback;

final class DispatchingBuilderTest {

    @Test
    void tracebackTest() {
        var throwable = new Throwable();

        var a = traceback(null);
        var b = traceback(throwable);

        assertThat(a).isEqualTo("null");
        assertThat(b).startsWith("java.lang.Throwable");
    }
}