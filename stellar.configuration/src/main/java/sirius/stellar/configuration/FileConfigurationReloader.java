package sirius.stellar.configuration;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import static java.lang.Runtime.getRuntime;
import static java.lang.String.join;
import static java.lang.System.err;
import static java.lang.Thread.currentThread;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.SECONDS;

/// Extension of [ConfigurationReloader] which reloads based on updates to files
/// on the filesystem relative to the working directory of the application,
/// which match the [#extensions()] of this reloader.
///
/// @see ConfigurationReloader
/// @see Configuration
public abstract class FileConfigurationReloader
	implements ConfigurationReloader {

	@Nullable
	private WatchService watcher;

	@Nullable
	private ScheduledExecutorService scheduler;

	/// Set of file extensions that can be recognized, from the current working
	/// directory, by this provider.
	///
	/// Each string must begin with a period, e.g. `".properties"`.
	/// For example, `return Set.of(".properties")`.
	///
	/// @since 1.0
	protected abstract Set<String> extensions();

	@Override
	public void wire() {
		try {
			this.watcher = FileSystems.getDefault().newWatchService();
			if (this.watcher == null) throw new UnsupportedOperationException();
			this.working().register(this.watcher, ENTRY_MODIFY);

			this.scheduler = newSingleThreadScheduledExecutor(this::thread);
			this.scheduler.scheduleWithFixedDelay(this::poll, 0L, 5L, SECONDS);

			getRuntime().addShutdownHook(new Thread(this::close));
		} catch (UnsupportedOperationException | RejectedExecutionException exception) {
			String missing = (exception instanceof UnsupportedOperationException)
					? "java.nio.file.WatchService/java.nio.file.FileSystem#newWatchService"
					: "java.util.concurrent.ScheduledExecutorService#scheduleWithFixedDelay";
			String extensions = join(", ", this.extensions());
			err.printf("%s unavailable, ignoring changes to %s%n", missing, extensions);
		} catch (IOException exception) {
			throw new IllegalStateException(exception);
		}
	}

	/// [ThreadFactory] to construct a daemon thread with the provided runnable.
	private Thread thread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.setDaemon(true);
		return thread;
	}

	/// Polls for new changes. Looped on a thread instantiated by [#wire()].
	private void poll() {
		if (currentThread().isInterrupted()) return;
		if (this.watcher == null) throw new IllegalStateException();

		try {
			List<WatchEvent<?>> events = this.watcher.take().pollEvents();
			boolean updated = events.stream()
					.filter(event -> event.kind() == ENTRY_MODIFY)
					.map(WatchEvent::context)
					.map(context -> (Path) context)
					.anyMatch(path -> this.extension(path.getFileName().toString()));
			if (updated) this.reload();
		} catch (InterruptedException exception) {
			throw new IllegalStateException("Configuration watcher interrupted", exception);
		}
	}

	/// Returns whether the provided [String] file name ends with one of the
	/// declared [#extensions()]. This method should not be overridden.
	///
	/// @since 1.0
	protected boolean extension(String name) {
		int i = name.indexOf(".");
		if (i < 0) return false;

		return this.extensions().contains(name.substring(i));
	}

	/// Returns a path to the current working directory.
	/// This method should not be overridden.
	///
	/// @since 1.0
	protected Path working() {
		return Path.of("./");
	}

	/// Shutdown hook that closes the scheduler and watcher.
	private void close() {
		try {
			if (this.watcher == null || this.scheduler == null) return;
			this.scheduler.shutdown();
			this.watcher.close();
		} catch (IOException exception) {
			throw new IllegalStateException("Fatal failure closing configuration reloader", exception);
		}
	}
}