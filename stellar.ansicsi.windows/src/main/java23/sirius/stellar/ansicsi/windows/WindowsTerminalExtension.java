package sirius.stellar.ansicsi.windows;

import sirius.stellar.ansicsi.TerminalExtension;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.util.Optional;

import static java.lang.System.getProperty;
import static java.lang.foreign.Linker.nativeLinker;
import static java.lang.foreign.SymbolLookup.libraryLookup;
import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;

/// (see main file for summary)
///
/// @implNote This source file contains the Java >23 Panama FFM based
/// implementation, a Java <23 JNR-FFI based implementation exists.
/// @see "/src/main/java"
public final class WindowsTerminalExtension
		implements TerminalExtension {

	private static final int
		// https://learn.microsoft.com/windows/console/getstdhandle
		STD_OUTPUT_HANDLE = -11,
		STD_ERROR_HANDLE = -12,

		// https://learn.microsoft.com/windows/console/high-level-console-modes
		ENABLE_VIRTUAL_TERMINAL_PROCESSING = 0x0004,

		// https://learn.microsoft.com/windows/win32/intl/code-page-identifiers
		CP_UTF8 = 65001;

	private static final FunctionDescriptor
		// https://learn.microsoft.com/windows/console/setconsoleoutputcp
		SetConsoleOutputCP_DESCRIPTOR = FunctionDescriptor.of(JAVA_INT, JAVA_INT),

		// https://learn.microsoft.com/windows/console/getstdhandle
		GetStdHandle_DESCRIPTOR = FunctionDescriptor.of(ADDRESS, JAVA_INT),

		// https://learn.microsoft.com/windows/console/getconsolemode
		GetConsoleMode_DESCRIPTOR = FunctionDescriptor.of(JAVA_INT, ADDRESS, ADDRESS),

		// https://learn.microsoft.com/windows/console/setconsolemode
		SetConsoleMode_DESCRIPTOR = FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_INT);

	private final Arena arena;
	private final Linker linker;
	private final SymbolLookup lookup;

	private final MethodHandle
		SetConsoleOutputCP,
		GetStdHandle,
		GetConsoleMode,
		SetConsoleMode;

	public WindowsTerminalExtension() {
		this.arena = Arena.ofShared();

		this.linker = nativeLinker();
		this.lookup = libraryLookup("kernel32", this.arena);

		this.SetConsoleOutputCP = this.lookup("SetConsoleOutputCP", SetConsoleOutputCP_DESCRIPTOR);
		this.GetStdHandle = this.lookup("GetStdHandle", GetStdHandle_DESCRIPTOR);
		this.GetConsoleMode = this.lookup("GetConsoleMode", GetConsoleMode_DESCRIPTOR);
		this.SetConsoleMode = this.lookup("SetConsoleMode", SetConsoleMode_DESCRIPTOR);
	}

	/// Create a downcall handle for invoking the function provided by name.
	private MethodHandle lookup(String name, FunctionDescriptor descriptor) {
		Optional<MemorySegment> segment = this.lookup.find(name);
		if (segment.isEmpty()) throw new IllegalStateException("No kernel32::" + name);
		return this.linker.downcallHandle(segment.get(), descriptor);
	}

	/// Shortcut for checking if the current operating system is Windows.
	private static boolean windows() {
		return getProperty("os.name", "")
				.toLowerCase()
				.contains("win");
	}

	@Override
	public void wire() throws Throwable {
		if (!windows()) return;
		try (this.arena) {
			this.SetConsoleOutputCP.invoke(CP_UTF8);

			MemorySegment
				stdout = (MemorySegment) this.GetStdHandle.invoke(STD_OUTPUT_HANDLE),
				stderr = (MemorySegment) this.GetStdHandle.invoke(STD_ERROR_HANDLE);

			MemorySegment found = this.arena.allocate(JAVA_INT);
			int updated;

			this.GetConsoleMode.invoke(stdout, found);
			updated = found.get(JAVA_INT, 0) | ENABLE_VIRTUAL_TERMINAL_PROCESSING;
			this.SetConsoleMode.invoke(stdout, updated);

			this.GetConsoleMode.invoke(stderr, found);
			updated = found.get(JAVA_INT, 0) | ENABLE_VIRTUAL_TERMINAL_PROCESSING;
			this.SetConsoleMode.invoke(stderr, updated);
		}
	}
}