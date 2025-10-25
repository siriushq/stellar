package sirius.stellar.javadoc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Deque;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.*;
import static java.nio.file.Files.*;

/// Domain implementation of [Javadoc].
final class DJavadoc implements Javadoc {

	private final Deque<Callable<InputStream>> sources;
	private ExecutorService executor;

	DJavadoc() {
		this.sources = new ConcurrentLinkedDeque<>();
		this.executor = Executors.newVirtualThreadPerTaskExecutor();
	}

	@Override
	public Javadoc source(Path file) {
		this.sources.push(() -> newInputStream(file));
		return this;
	}

	@Override
	public Javadoc source(byte[] data) {
		this.sources.push(() -> new ByteArrayInputStream(data));
		return this;
	}

	@Override
	public Javadoc source(String data) {
		this.sources.push(() -> new ByteArrayInputStream(data.getBytes(UTF_8)));
		return this;
	}

	@Override
	public Javadoc sources(Path directory) {
		try (Stream<Path> stream = Files.walk(directory)
				.filter(Files::isRegularFile)
				.filter(path -> path.getFileName().toString().endsWith(".java"))) {
			stream.forEach(this::source);
		} catch (IOException exception) {
			throw new IllegalStateException("Failed to walk source code directory provided to Javadoc", exception);
		}
		return this;
	}

	@Override
	public Javadoc executor(ExecutorService executor) {
		this.executor.close();
		this.executor = executor;
		return this;
	}

	@Override
	public void generate(OutputStream output) {

	}

	@Override
	public void generate(Path path) {

	}

	@Override
	public void close() {
		this.executor.close();
	}
}