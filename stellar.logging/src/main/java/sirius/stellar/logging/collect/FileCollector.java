package sirius.stellar.logging.collect;

import org.jspecify.annotations.Nullable;
import sirius.stellar.logging.LoggerMessage;

import java.io.IOException;
import java.io.Serial;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.charset.StandardCharsets.*;
import static java.time.Instant.*;
import static java.util.stream.Collectors.*;
import static sirius.stellar.logging.Logger.*;

/// Implementation of [Collector] that prints to log files.
final class FileCollector implements Collector {

	@Serial
	private static final long serialVersionUID = 4479392734705305030L;

	private final Path path;
	private final Duration duration;

	private final AtomicBoolean closing;
	private final AtomicBoolean writing;

	private @Nullable FileChannel channel;
	private @Nullable Instant rolled;

	FileCollector(Path path, Duration duration) {
		this.path = path;
		this.duration = duration;

		this.closing = new AtomicBoolean();
		this.writing = new AtomicBoolean();

		this.roll();
	}

	@Override
	public void collect(LoggerMessage message) {
		if (this.closing.get()) return;
		this.writing.set(true);

		assert this.rolled != null;
		if (this.rolled.plus(this.duration).isBefore(now())) this.roll();

		task(() -> this.write(message));
	}

	/// Write the provided message to the current file.
	/// This is run in [#collect(LoggerMessage)].
	private void write(LoggerMessage message) {
		try {
			StringJoiner joiner = new StringJoiner("\",\"", "\"", "\"\n");
			joiner.add(message.time().toString())
				  .add(message.level().toString())
				  .add(message.thread())
				  .add(message.name());
			String lines = Arrays.stream(message.text()
					.replaceAll("\"", "`")
					.replaceAll("'", "`")
					.split("\n"))
					.map(line -> "'" + line + "'")
					.collect(joining());
			joiner.add(lines);

			byte[] text = joiner.toString().getBytes(UTF_8);

			assert this.channel != null;
			int written = this.channel.write(ByteBuffer.wrap(text));
			if (written != text.length) throw new IOException("Written size mismatch");

			this.writing.set(false);
		} catch (IOException exception) {
			throw new IllegalStateException("Failed to append to file", exception);
		}
	}

	@Override
	public void close() {
		try {
			this.closing.set(true);
			while (this.writing.get()) Thread.onSpinWait();

			assert this.channel != null;
			this.channel.close();
		} catch (IOException exception) {
			throw new IllegalStateException("Failed to close file logger collector", exception);
		}
	}

	/// Rolls the internal file channel for this collector to allow for a new
	/// file to be used and the old one to serve as an archive for the previous
	/// duration of logging.
	private void roll() {
		try {
			if (this.channel != null && this.channel.isOpen()) this.channel.close();
			this.rolled = now();

			Path file = this.path.resolve(this.rolled.toEpochMilli() + "-" + UUID.randomUUID() + ".csv");
			Files.createDirectories(this.path);
			Files.createFile(file);
			this.channel = FileChannel.open(file, StandardOpenOption.APPEND);

			byte[] header = "\"time\",\"level\",\"thread\",\"name\",\"text\"\n".getBytes(UTF_8);
			int written = this.channel.write(ByteBuffer.wrap(header));
			if (written != header.length) throw new IOException("Written size mismatch, writing file header");
		} catch (IOException exception) {
			throw new IllegalStateException("Failed to roll file logger collector", exception);
		}
	}
}