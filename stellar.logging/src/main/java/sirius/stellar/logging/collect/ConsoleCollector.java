package sirius.stellar.logging.collect;

import org.jspecify.annotations.Nullable;
import sirius.stellar.facility.Strings;
import sirius.stellar.facility.doctation.Internal;
import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;
import sirius.stellar.logging.LoggerMessage;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;

import static sirius.stellar.facility.Strings.*;
import static sirius.stellar.facility.terminal.TerminalColor.*;

/**
 * Implementation of {@link Collector} that prints to {@link System#out}.
 * <p>
 * Only one instance of this class should ever be created as when creating
 * an instance, the {@link System#setOut(PrintStream)} method as well as
 * the {@link System#setErr(PrintStream)} method should be called.
 */
@Internal
final class ConsoleCollector implements Collector {

	@Serial
	private static final long serialVersionUID = -6081062057103191874L;

	private final PrintStream stream;

	private ConsoleCollector(PrintStream stream) {
		this.stream = stream;
	}

	/**
	 * Returns an instance of this collector. This can only be called once across
	 * the application lifecycle as {@link System#out} and {@link System#err} are
	 * overridden statically.
	 * <p>
	 * Overrides {@link System#out} and {@link System#err} with implementations
	 * that will pass messages through to the logger at appropriate levels.
	 */
	public static ConsoleCollector get() {
		ConsoleCollector collector = new ConsoleCollector(System.out);
		System.setOut(new DelegatePrintStream(text -> Logger.dispatch(Instant.now(), LoggerLevel.INFORMATION, Thread.currentThread().getName(), "stdout", text)));
		System.setErr(new DelegatePrintStream(text -> Logger.dispatch(Instant.now(), LoggerLevel.ERROR, Thread.currentThread().getName(), "stderr", text)));
		return collector;
	}

	@Override
	public void collect(LoggerMessage message) {
		this.stream.println(format(
				"{5}[{6}{0,date,dd/MM/yyyy HH:mm:ss} {5}| {6}{1} {5}| {6}{2} {5}| {6}{3}{5}] {7}{4}",
				Date.from(message.time()),
				switch (message.level()) {
					case ALL, INFORMATION -> BLUE.foreground().bright() + message.level().display();
					case WARNING -> YELLOW.foreground().bright() + message.level().display();
					case ERROR, STACKTRACE -> RED.foreground().bright() + message.level().display();
					case DEBUGGING, CONFIGURATION -> MAGENTA.foreground().bright() + message.level().display();
					case OFF -> message.level().display();
				},
				message.thread(),
				message.name(),
				message.text(),

				BLACK.foreground().bright(),
				WHITE.foreground().dark(),
				DEFAULT.foreground().bright()
		));
	}

	@Override
	public String toString() {
		return format("ConsoleCollector[stream={0}]", this.stream.toString());
	}
}

/**
 * Implementation of {@link PrintStream} intended to replace {@link System#out}/{@link System#err}.
 * <p>
 * {@link PrintStream#println()} is replaced with a no-op as empty log messages are discarded anyway.
 * {@link PrintStream#print} and {@link PrintStream#append} methods will perform the equivalent of {@code println}.
 * {@link PrintStream#format} methods will perform the equivalent of {@link PrintStream#printf}.
 * {@code PrintStream#write} methods are not implemented at all, and are completely discarded.
 * <p>
 * This implements {@link Serializable} - while it is not a semantic use of a {@link PrintStream} to serialize
 * the stream (and subsequently write a stream inside a stream), it is quite a common scenario for this to be
 * done on accident, and it is perfectly fine to serialize this object. Accidentally serializing this object
 * can be done if, say, a logger object from logging facade that a dispatcher is available for is stored as an
 * instance variable - serializing the logger object could cause {@code System.out} or {@code System.err} to be
 * serialized and, potentially, subsequently, this class serialized.
 */
@Internal
final class DelegatePrintStream extends PrintStream implements Serializable {

	@Serial
	private static final long serialVersionUID = 163954357471100L;

	private final Consumer<String> dispatcher;

	DelegatePrintStream(Consumer<String> dispatcher) {
		super(OutputStream.nullOutputStream());
		this.dispatcher = dispatcher;
	}

	//#region println* [delegates to print*]
	@Override
	public void println() {
		assert true;
	}

	@Override
	public void println(boolean b) {
		this.print(b);
	}

	@Override
	public void println(char c) {
		this.print(c);
	}

	@Override
	public void println(int i) {
		this.print(i);
	}

	@Override
	public void println(long l) {
		this.print(l);
	}

	@Override
	public void println(float f) {
		this.print(f);
	}

	@Override
	public void println(double d) {
		this.print(d);
	}

	@Override
	public void println(char[] text) {
		this.print(text);
	}

	@Override
	public void println(@Nullable String text) {
		this.print(text);
	}

	@Override
	public void println(@Nullable Object object) {
		this.print(object);
	}
	//#endregion
	//#region append* [delegates to print*]
	@Override
	public PrintStream append(char c) {
		this.print(c);
		return this;
	}

	@Override
	public PrintStream append(@Nullable CharSequence sequence) {
		this.print(sequence);
		return this;
	}

	@Override
	public PrintStream append(@Nullable CharSequence sequence, int start, int end) {
		this.print(sequence == null ? "null" : sequence.subSequence(start, end));
		return this;
	}
	//#endregion
	//#region format*
	@Override
	public PrintStream format(@Nullable String text, Object... arguments) {
		this.dispatcher.accept(Strings.format(String.valueOf(text), arguments));
		return this;
	}

	@Override
	public PrintStream format(Locale locale, @Nullable String text, Object... arguments) {
		this.dispatcher.accept(Strings.format(locale, String.valueOf(text), arguments));
		return this;
	}
	//#endregion [

	//#region printf*
	@Override
	public PrintStream printf(@Nullable String text, Object... arguments) {
		this.dispatcher.accept(Strings.format(String.valueOf(text), arguments));
		return this;
	}

	@Override
	public PrintStream printf(Locale locale, @Nullable String text, Object... arguments) {
		this.dispatcher.accept(Strings.format(locale, String.valueOf(text), arguments));
		return this;
	}
	//#endregion
	//#region print*
	@Override
	public void print(boolean b) {
		this.dispatcher.accept(String.valueOf(b));
	}

	@Override
	public void print(char c) {
		this.dispatcher.accept(String.valueOf(c));
	}

	@Override
	public void print(int i) {
		this.dispatcher.accept(String.valueOf(i));
	}

	@Override
	public void print(long l) {
		this.dispatcher.accept(String.valueOf(l));
	}

	@Override
	public void print(float f) {
		this.dispatcher.accept(String.valueOf(f));
	}

	@Override
	public void print(double d) {
		this.dispatcher.accept(String.valueOf(d));
	}

	@Override
	public void print(char[] text) {
		this.dispatcher.accept(String.valueOf(text));
	}

	@Override
	public void print(@Nullable String text) {
		this.dispatcher.accept(String.valueOf(text));
	}

	@Override
	public void print(@Nullable Object object) {
		this.dispatcher.accept(String.valueOf(object));
	}
	//#endregion
}