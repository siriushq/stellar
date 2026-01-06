package sirius.stellar.logging.fluent;

import org.jspecify.annotations.Nullable;
import sirius.stellar.annotation.Contract;
import sirius.stellar.annotation.Internal;
import sirius.stellar.logging.Logger;
import sirius.stellar.logging.LoggerLevel;
import sirius.stellar.logging.LoggerMessage;
import sirius.stellar.logging.LoggerMessage.Builder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;

import static java.util.Objects.requireNonNull;

/// Default implementation of [Builder].
@Internal
public final class DispatchingBuilder implements Builder {

	@Nullable
	private Instant time;

	@Nullable
	private LoggerLevel level;

	@Nullable
	private String thread;

	@Nullable
	private String name;

	@Nullable
	private String text;

	@Override
	public LoggerMessage build() {
		Instant time = requireNonNull(this.time);
		LoggerLevel level = requireNonNull(this.level);
		String thread = requireNonNull(this.thread);
		String name = requireNonNull(this.name);
		String text = requireNonNull(this.text);
		return new LoggerMessage(time, level, thread, name, text);
	}

	@Override
	public void dispatch() {
		Logger.dispatch(this.build());
	}

	@Override
	public Builder level(LoggerLevel level) {
		if (!Logger.enabled(level)) return new DisabledBuilder();
		this.level = level;
		return this;
	}

	@Override
	public Builder time(Instant time) {
		this.time = time;
		return this;
	}

	@Override
	public Builder thread(String thread) {
		this.thread = thread;
		return this;
	}

	@Override
	public Builder name(String name) {
		this.name = name;
		return this;
	}

	@Override
	public Builder text(String text) {
		this.text = text;
		return this;
	}

	@Override
	public Builder throwable(Throwable throwable) {
		if (this.text == null) this.text = "";
		this.text += "\n" + traceback(throwable);
		return this;
	}

	/// Returns a stacktrace string for the provided throwable.
	///
	/// The format of this information depends on the implementation, see
	/// [Throwable#printStackTrace] for more details. but the following
	/// example may be regarded as typical:
	///
	/// ```
	/// HighLevelException: MidLevelException: LowLevelException
	///     at Junk.a(Junk.java:13)
	///     at Junk.main(Junk.java:4)
	/// Caused by: MidLevelException: LowLevelException
	///     at Junk.c(Junk.java:23)
	///     at Junk.b(Junk.java:17)
	///     at Junk.a(Junk.java:11)
	///     ... 1 more
	/// Caused by: LowLevelException
	///     at Junk.e(Junk.java:30)
	///     at Junk.d(Junk.java:27)
	///     at Junk.c(Junk.java:21)
	///     ... 3 more
	/// ```
	///
	/// @see Throwable#printStackTrace()
	@Contract("_ -> new")
	static String traceback(@Nullable Throwable throwable) {
		if (throwable == null) return "null";
		StringWriter writer = new StringWriter();
		try (PrintWriter printWriter = new PrintWriter(writer)) {
			throwable.printStackTrace(printWriter);
			return writer.toString();
		}
	}
}