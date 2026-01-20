package sirius.stellar.logging.collect.file;

import org.jspecify.annotations.Nullable;
import sirius.stellar.logging.LoggerMessage;
import sirius.stellar.logging.spi.LoggerCollector;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.ServiceLoader;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.time.Instant.now;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.joining;

/// Implementation of [LoggerCollector] for logging to files.
/// This will roll twice a day.
///
/// @see FileTechnique
/// @since 1.0
public final class FileCollector implements LoggerCollector {

	private final FileTechnique technique;

	private final AtomicBoolean closing;
	private final ReentrantLock writing;

	@Nullable
	private FileChannel channel;

	@Nullable
	private Instant rolled;

	public FileCollector() {
		this.technique = ServiceLoader.load(FileTechnique.class)
				.findFirst()
				.orElseGet(PlainFileTechnique::new);

		this.closing = new AtomicBoolean();
		this.writing = new ReentrantLock();

		this.roll();
	}

	@Override
	public void collect(LoggerMessage message) {
		if (this.closing.get()) return;
		this.writing.lock();

		try {
			assert this.rolled != null;
			assert this.channel != null;

			if (this.rolled.plus(Duration.ofHours(12)).isBefore(now())) this.roll();
			byte[] text = this.technique.format(message).getBytes(UTF_8);

			int written = this.channel.write(ByteBuffer.wrap(text));
			if (written != text.length) throw new IOException("Written size mismatch");
		} catch (IOException exception) {
			throw new IllegalStateException("Failed to append to file", exception);
		} finally {
			this.writing.unlock();
		}
	}

	/// Rolls the internal file channel for this collector, to allow for a new
	/// file to be used, and the old one to serve as an archive for the previous
	/// duration of logging.
	private void roll() {
		try {
			if (this.channel != null && this.channel.isOpen()) this.channel.close();

			this.rolled = Instant.now();
			long now = this.rolled.toEpochMilli();

			Path directory = this.technique.directory();
			String extension = this.technique.extension();
			Path file = directory.resolve(now + "-" + randomUUID() + extension);

			Files.createDirectories(directory);
			Files.createFile(file);

			this.channel = FileChannel.open(file, APPEND);
			if (this.channel == null) throw new IOException("FileChannel#open returned null");

			byte[] header = this.technique.header().getBytes(UTF_8);
			int written = this.channel.write(ByteBuffer.wrap(header));
			if (written != header.length) throw new IOException("Written size mismatch");
		} catch (IOException exception) {
			throw new IllegalStateException("Failed to roll file logger collector", exception);
		}
	}

	@Override
	public void close() {
		this.writing.lock();
		try {
			this.closing.set(true);

			assert this.channel != null;
			this.channel.close();
		} catch (IOException exception) {
			throw new IllegalStateException("Failed to close file logger collector", exception);
		} finally {
			this.writing.unlock();
		}
	}
}