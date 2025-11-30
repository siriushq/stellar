package sirius.stellar.logging.collect;

import org.jspecify.annotations.Nullable;
import sirius.stellar.facility.Strings;
import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerMessage;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;

import static java.lang.Thread.*;
import static sirius.stellar.facility.Strings.*;
import static sirius.stellar.facility.terminal.TerminalColor.*;
import static sirius.stellar.logging.Logger.*;
import static sirius.stellar.logging.LoggerLevel.*;

/// Implementation of [Collector] that prints to [System#out].
///
/// Only one instance of this class should ever be created as when creating
/// an instance, the [System#setOut(PrintStream)] method as well as
/// the [System#setErr(PrintStream)] method should be called.
final class ConsoleCollector implements Collector {

	@Serial
	private static final long serialVersionUID = -6081062057103191874L;

	private final PrintStream stream;

	/// Returns an instance of this collector for the provided [PrintStream].
	ConsoleCollector(PrintStream stream) {
		this.stream = stream;
	}

	/// Returns an instance of this collector for the provided [PrintStream], overriding
	/// [System#out] (`stdout`) and [System#err] (`stderr`) with a stream that dispatches
	/// further writes to [Logger] at appropriate logger levels.
	///
	/// This can only be called once across the application lifecycle.
	/// @param stream the stream to collect to, e.g. [System#err]
	static ConsoleCollector overriding(PrintStream stream) {
		ConsoleCollector collector = new ConsoleCollector(stream);
		System.setOut(new DelegatePrintStream(text -> dispatch(Instant.now(), INFORMATION, currentThread().getName(), "stdout", text)));
		System.setErr(new DelegatePrintStream(text -> dispatch(Instant.now(), ERROR, currentThread().getName(), "stderr", text)));
		return collector;
	}

	@Override
	public void collect(LoggerMessage message) {
		this.stream.println(format(
			"{5}[{6}{0,date,dd/MM/yyyy HH:mm:ss} {5}| {6}{1} {5}| {6}{2} {5}| {6}{3}{5}] {7}{4}{8}",
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
			WHITE.foreground().bright(),
			DEFAULT.foreground().bright()
		));
	}

	@Override
	public String toString() {
		return format("ConsoleCollector[stream={0}]", this.stream.toString());
	}
}

/// Implementation of [PrintStream] intended to replace [System#out]/[System#err].
///
/// [PrintStream#println()] is replaced with a no-op as empty log messages are discarded anyway.
/// [PrintStream#print] and [PrintStream#append] methods will perform the equivalent of `println`.
/// [PrintStream#format] methods will perform the equivalent of [PrintStream#printf].
/// [PrintStream#write] methods are not implemented at all, and are completely discarded.
///
/// This implements [Serializable] - while it is not a semantic use of a [PrintStream] to serialize
/// the stream (and subsequently write a stream inside a stream), it is quite a common scenario for
/// this to be done on accident, and it is perfectly fine to serialize this object.
///
/// Accidentally serializing this object can be done if, say, a logger object from logging facade
/// that a dispatcher is available for, is stored as an instance variable - serializing the logger
/// object could cause `System.out` or `System.err` to be serialized, and subsequently,
/// this class serialized.
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