package sirius.stellar.configuration;

import io.avaje.spi.ServiceProvider;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import static java.lang.Runtime.*;
import static java.lang.System.*;
import static java.lang.Thread.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.util.concurrent.Executors.*;
import static java.util.concurrent.TimeUnit.*;

/// Implementation of [ConfigurationReloader] which reloads based on updates to `.properties`
/// files on the filesystem relative to the working directory of the application.
///
/// @see Configuration
@ServiceProvider
public final class PropertiesConfigurationReloader implements ConfigurationReloader, Runnable {

	@Nullable
	private WatchService watcher;

	@Nullable
	private ScheduledExecutorService scheduler;

	@Override
	public void wire() throws Throwable {
		Path working = Path.of("./");

		try {
			this.watcher = FileSystems.getDefault().newWatchService();
			working.register(this.watcher, ENTRY_MODIFY);

			this.scheduler = newSingleThreadScheduledExecutor(this::thread);
			this.scheduler.scheduleWithFixedDelay(this, 0L, 5L, SECONDS);

			getRuntime().addShutdownHook(new Thread(this::close));
		} catch (UnsupportedOperationException | RejectedExecutionException exception) {
			String missing = (exception instanceof UnsupportedOperationException)
					? "java.nio.file.WatchService/java.nio.file.FileSystem#newWatchService"
					: "java.util.concurrent.ScheduledExecutorService#scheduleWithFixedDelay";
			err.printf("%s unavailable, ignoring .properties changes\n", missing);
		}
	}

	@Override
	public void run() {
		if (currentThread().isInterrupted()) return;
		if (this.watcher == null) throw new IllegalStateException();

		try {
			List<WatchEvent<?>> events = this.watcher.take().pollEvents();
			boolean updated = events.stream()
					.filter(event -> event.kind() == ENTRY_MODIFY)
					.map(WatchEvent::context)
					.map(context -> (Path) context)
					.anyMatch(path -> path.getFileName().toString().endsWith(".properties"));
			if (updated) this.reload();
		} catch (InterruptedException exception) {
			throw new IllegalStateException("Configuration watcher interrupted", exception);
		}
	}

	/// [ThreadFactory] to construct a daemon thread with the provided runnable.
	private Thread thread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.setDaemon(true);
		return thread;
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