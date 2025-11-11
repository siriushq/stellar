package sirius.stellar.facility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

final class ThrowablesTest {

    //#region forEach(Throwable)
    @Test @DisplayName("forEach(Throwable) correctly iterates over causes")
    void forEachCorrectlyIteratesOverCauses() {
        var cause = new Throwable("A cause");
        var throwable = new Throwable(cause);

        var result = new ArrayList<Throwable>();
        Throwables.forEach(throwable, result::add);

        assertThat(result).hasSize(2);
    }

    @Test @DisplayName("forEach(Throwable) correctly handles a recursive cause structure")
    void forEachCorrectlyHandlesRecursiveCauseStructure() {
        var a = new Throwable();
        var b = new Throwable(a);
        a.initCause(b);

        var result = new ArrayList<Throwable>();
        Throwables.forEach(b, result::add);

        assertThat(result).hasSize(2);
    }
    //#endregion

    //#region causes(Throwable)
    @Test @DisplayName("causes(Throwable) correctly returns a list of causes")
    void causesCorrectlyReturnsAListOfCauses() {
        var cause = new Throwable("A cause");
        var throwable = new Throwable(cause);

        var list = Throwables.causes(throwable);

        assertThat(list).hasSize(2);
    }

    @Test @DisplayName("causes(Throwable) correctly handles a recursive cause structure")
    void causesCorrectlyHandlesRecursiveCauseStructure() {
        var a = new Throwable();
        var b = new Throwable(a);
        a.initCause(b);

        var list = Throwables.causes(b);

        assertThat(list).hasSize(2);
    }
    //#endregion

    //#region stream(Throwable)
    @Test @DisplayName("stream(Throwable) correctly returns a stream of causes")
    void streamCorrectlyReturnsAStreamOfCauses() {
        var cause = new Throwable("A cause");
        var throwable = new Throwable(cause);

        var list = Throwables.stream(throwable).collect(toList());

        assertThat(list).hasSize(2);
    }

    @Test @DisplayName("stream(Throwable) correctly handles a recursive cause structure")
    void streamCorrectlyHandlesRecursiveCauseStructure() {
        var a = new Throwable();
        var b = new Throwable(a);
        a.initCause(b);

        var list = Throwables.stream(b).toList();

        assertThat(list).hasSize(2);
    }

    @Test @DisplayName("stream(Throwable) correctly handles cycles in the cause chain tail")
    void streamCorrectlyHandlesCyclesInTheCauseChainTail() {
        var a = new Throwable("A");
        var b = new Throwable("B");
        b.initCause(a);
        a.initCause(b);

        var root = new Throwable("Root");
        root.initCause(b);

        var list = Throwables.stream(root).toList();

        assertThat(list).containsExactly(root, b, a);
        assertThat(list).hasSize(3);
    }
    //#endregion

    //#region stacktrace(Throwable)
    @Test @DisplayName("stacktrace(Throwable) outputs a stacktrace and is null-safe")
    void stacktraceOutputsAStacktraceAndIsNullSafe() {
        var throwable = new Throwable();

        var a = Throwables.stacktrace(null);
        var b = Throwables.stacktrace(throwable);

        assertThat(a).isEqualTo("null");
        assertThat(b).startsWith("java.lang.Throwable");
    }
    //#endregion
}