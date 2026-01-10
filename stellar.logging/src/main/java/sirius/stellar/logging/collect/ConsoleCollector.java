package sirius.stellar.logging.collect;

import org.jspecify.annotations.Nullable;
import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;
import sirius.stellar.logging.LoggerMessage;

import java.io.PrintStream;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

import static java.lang.Thread.currentThread;
import static sirius.stellar.facility.terminal.TerminalColor.*;
import static sirius.stellar.logging.Logger.format;
import static sirius.stellar.logging.LoggerLevel.ERROR;
import static sirius.stellar.logging.LoggerLevel.INFORMATION;

/// Implementation of [Collector] that prints to a given [PrintStream],
/// and optionally can override [System#out] and [System#err].
final class ConsoleCollector implements Collector {

	private final PrintStream stream;

	/// Instantiate this collector for the provided [PrintStream].
	/// @see #overriding(PrintStream)
	ConsoleCollector(PrintStream stream) {
		this.stream = stream;
	}

	/// Instantiate this collector for the provided [PrintStream], overriding
	/// [System#out] (`stdout`) and [System#err] (`stderr`) with streams that
	/// dispatch further writes to [Logger].
	///
	/// This should only be called once across the application lifecycle.
	/// @see #ConsoleCollector(PrintStream)
	static Collector overriding(PrintStream stream) {
		Collector collector = new ConsoleCollector(stream);
		System.setOut(new DispatchingPrintStream(INFORMATION, "stdout"));
		System.setErr(new DispatchingPrintStream(ERROR, "stderr"));
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
				case ERROR, TRACING -> RED.foreground().bright() + message.level().display();
				case DIAGNOSIS, CONFIGURATION -> MAGENTA.foreground().bright() + message.level().display();
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

/// Implementation of [PrintStream] intended to replace both [System#out]
/// and [System#err], dispatching to the [Logger] instead. Note that:
///
/// - [PrintStream#println()] is replaced with a no-op, as empty
///   logging messages are discarded anyway.
/// - [PrintStream#print] & [PrintStream#append] methods will perform the
///   equivalent of `println`.
/// - [PrintStream#format] methods will perform the equivalent of
///   [PrintStream#printf].
/// - [PrintStream#write] methods are not implemented at all,
///   and are completely discarded.
final class DispatchingPrintStream extends PrintStream {

	private final LoggerLevel level;
	private final String name;

	/// Create a dispatching stream that will log to the provided level,
	/// with the provided logger name as an alias.
	DispatchingPrintStream(LoggerLevel level, String name) {
		super(nullOutputStream());
		this.level = level;
		this.name = name;
	}

	/// Dispatch the provided text at [#level].
	private void dispatch(String text) {
		LoggerMessage.builder()
				.level(this.level)
				.time(Instant.now())
				.thread(currentThread().getName())
				.name(this.name)
				.text(text)
				.dispatch();
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
		this.dispatch(Logger.format(String.valueOf(text), arguments));
		return this;
	}

	@Override
	public PrintStream format(Locale locale, @Nullable String text, Object... arguments) {
		this.dispatch(Logger.format(locale, String.valueOf(text), arguments));
		return this;
	}
	//#endregion [

	//#region printf*
	@Override
	public PrintStream printf(@Nullable String text, Object... arguments) {
		this.dispatch(Logger.format(String.valueOf(text), arguments));
		return this;
	}

	@Override
	public PrintStream printf(Locale locale, @Nullable String text, Object... arguments) {
		this.dispatch(Logger.format(locale, String.valueOf(text), arguments));
		return this;
	}
	//#endregion
	//#region print*
	@Override
	public void print(boolean b) {
		this.dispatch(String.valueOf(b));
	}

	@Override
	public void print(char c) {
		this.dispatch(String.valueOf(c));
	}

	@Override
	public void print(int i) {
		this.dispatch(String.valueOf(i));
	}

	@Override
	public void print(long l) {
		this.dispatch(String.valueOf(l));
	}

	@Override
	public void print(float f) {
		this.dispatch(String.valueOf(f));
	}

	@Override
	public void print(double d) {
		this.dispatch(String.valueOf(d));
	}

	@Override
	public void print(char[] text) {
		this.dispatch(String.valueOf(text));
	}

	@Override
	public void print(@Nullable String text) {
		this.dispatch(String.valueOf(text));
	}

	@Override
	public void print(@Nullable Object object) {
		this.dispatch(String.valueOf(object));
	}
	//#endregion
}