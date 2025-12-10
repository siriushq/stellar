package sirius.stellar.facility.executor;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

/// Executes all submitted tasks directly in the same thread as the caller.
///
/// Running [#shutdown()] does not prevent new tasks from being submitted.
/// This allows for this implementation to trivially remain thread-safe (no shared state).
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class SynchronousExecutorService extends AbstractExecutorService {

	private volatile boolean terminated;

    @Override
    public void shutdown() {
        terminated = true;
    }

    @Override
    public boolean isShutdown() {
        return terminated;
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) {
        this.shutdown();
        return terminated;
    }

    @Override
    public List<Runnable> shutdownNow() {
        return Collections.emptyList();
    }

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }
}