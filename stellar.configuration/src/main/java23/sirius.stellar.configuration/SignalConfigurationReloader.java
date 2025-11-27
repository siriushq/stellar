package sirius.stellar.configuration;

import io.avaje.spi.ServiceProvider;
import org.jspecify.annotations.Nullable;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Runtime.*;
import static java.lang.System.*;
import static java.lang.Thread.*;
import static java.lang.foreign.MemorySegment.*;
import static java.lang.foreign.ValueLayout.*;
import static java.util.concurrent.Executors.*;
import static java.util.concurrent.TimeUnit.*;

/// Implementation of [ConfigurationReloader] binding to `SIGHUP` via the
/// POSIX APIs using the Foreign Function and Memory API (on Java >23).
///
/// @see Configuration
@ServiceProvider
public final class SignalConfigurationReloader implements ConfigurationReloader, Runnable {

	/// The UNIX-standard signal number for `SIGHUP`.
	private static final int SIGHUP = 1;

	/// Descriptor of `struct sigaction` from POSIX `signal.h`.
	private static final FunctionDescriptor SIGACTION = FunctionDescriptor.of(JAVA_INT, JAVA_INT, ADDRESS, ADDRESS);

	/// An assumed size of `struct sigaction`, the least required to be portable.
	private static final int SIGACTION_SIZE = 16;

	/// Descriptor of function pointer type `void (*sa_handler)(int)`.
	private static final FunctionDescriptor SIGACTION_HANDLER = FunctionDescriptor.ofVoid(JAVA_INT);

	/// Description of Java method [#receive(int)] for upcall stub.
	private static final MethodType RECEIVE = MethodType.methodType(void.class, int.class);

	@Nullable
	private AtomicInteger signum;

	@Nullable
	private ScheduledExecutorService scheduler;

	/// Receives the signal. This method is exposed via FFM upcall stub and must only
	/// ever contain thread-safe operations, e.g. setting atomic value [#signum].
	private void receive(int signum) {
		if (this.signum == null || signum != SIGHUP) return;
		this.signum.set(signum);
	}

	/// [ThreadFactory] to construct a daemon thread with the provided runnable.
	private Thread thread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.setDaemon(true);
		return thread;
	}

	@Override
	public void run() {
		if (this.signum == null || currentThread().isInterrupted()) return;

		int signum = this.signum.getAndSet(0);
		if (signum != 0) this.reload();
		onSpinWait();
	}

	@Override
	public void wire() {
		try {
			if (getProperty("os.name").startsWith("Windows")) return;

			this.signum = new AtomicInteger(0);
			this.scheduler = newSingleThreadScheduledExecutor(this::thread);
			this.scheduler.scheduleWithFixedDelay(this, 0L, 1L, SECONDS);
			getRuntime().addShutdownHook(new Thread(this::close));

			Arena arena = Arena.global();
			Linker linker = Linker.nativeLinker();

			SymbolLookup posix = linker.defaultLookup();
			MethodHandle sigaction = posix.find("sigaction")
					.map(symbol -> linker.downcallHandle(symbol, SIGACTION))
					.orElseThrow(IllegalStateException::new);

			MethodHandle receiveHandle = MethodHandles.lookup()
					.findVirtual(SignalConfigurationReloader.class, "receive", RECEIVE);
			MemorySegment receiveUpcall = linker.upcallStub(receiveHandle, SIGACTION_HANDLER, arena);

			MemorySegment sigactionStruct = arena.allocate(SIGACTION_SIZE);
			sigactionStruct.set(ADDRESS, 0, receiveUpcall);

			int result = (int) sigaction.invokeExact(SIGHUP, sigactionStruct, NULL);
			if (result != 0) throw new IllegalStateException();
		} catch (Throwable ignored) {
			err.println("Using JVM >23, and failed to register POSIX signal handler, ignoring SIGHUP");
		}
	}

	/// Shutdown hook that closes the scheduler.
	private void close() {
		if (this.scheduler == null) return;
		this.scheduler.close();
	}
}