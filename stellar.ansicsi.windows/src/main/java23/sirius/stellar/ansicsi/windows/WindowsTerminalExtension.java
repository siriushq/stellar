package sirius.stellar.ansicsi.windows;

import sirius.stellar.ansicsi.TerminalExtension;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.util.Optional;
import java.util.function.BiFunction;

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
		STD_OUTPUT_HANDLE = -11, STD_ERROR_HANDLE = -12,
		// https://learn.microsoft.com/windows/console/high-level-console-modes
		ENABLE_VIRTUAL_TERMINAL_PROCESSING = 0x0004,
		// https://learn.microsoft.com/windows/win32/intl/code-page-identifiers
		CP_UTF8 = 65001;

	private static final BiFunction<Linker, SymbolLookup, MethodHandle>
		// https://learn.microsoft.com/windows/console/setconsoleoutputcp
		SetConsoleOutputCP_FINDER = downcall("SetConsoleOutputCP", JAVA_INT, JAVA_INT),
		// https://learn.microsoft.com/windows/console/getstdhandle
		GetStdHandle_FINDER = downcall("GetStdHandle", ADDRESS, JAVA_INT),
		// https://learn.microsoft.com/windows/console/getconsolemode
		GetConsoleMode_FINDER = downcall("GetConsoleMode", JAVA_INT, ADDRESS, ADDRESS),
		// https://learn.microsoft.com/windows/console/setconsolemode
		SetConsoleMode_FINDER = downcall("SetConsoleMode", JAVA_INT, ADDRESS, JAVA_INT);

	/// Generate a finder function which, when provided with a [Linker] and
	/// [SymbolLookup], will return a [MethodHandle] for a given function,
	/// described when invoking this method.
	private static BiFunction<Linker, SymbolLookup, MethodHandle>
	downcall(String name, MemoryLayout result, MemoryLayout... arguments) {
		return (linker, lookup) -> {
			FunctionDescriptor descriptor = FunctionDescriptor.of(result, arguments);
			Optional<MemorySegment> segment = lookup.find(name);
			if (segment.isEmpty()) throw new IllegalStateException("No kernel32::" + name);
			return linker.downcallHandle(segment.get(), descriptor);
		};
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
		try (Arena arena = Arena.ofConfined()) {
			Linker linker = nativeLinker();
			SymbolLookup lookup = libraryLookup("kernel32", arena);

			MethodHandle
				SetConsoleOutputCP = SetConsoleOutputCP_FINDER.apply(linker, lookup),
				GetStdHandle = GetStdHandle_FINDER.apply(linker, lookup),
				GetConsoleMode = GetConsoleMode_FINDER.apply(linker, lookup),
				SetConsoleMode = SetConsoleMode_FINDER.apply(linker, lookup);

			SetConsoleOutputCP.invoke(CP_UTF8);

			MemorySegment
				stdout = (MemorySegment) GetStdHandle.invoke(STD_OUTPUT_HANDLE),
				stderr = (MemorySegment) GetStdHandle.invoke(STD_ERROR_HANDLE);

			MemorySegment found = arena.allocate(JAVA_INT);
			int updated;

			GetConsoleMode.invoke(stdout, found);
			updated = found.get(JAVA_INT, 0) | ENABLE_VIRTUAL_TERMINAL_PROCESSING;
			SetConsoleMode.invoke(stdout, updated);

			GetConsoleMode.invoke(stderr, found);
			updated = found.get(JAVA_INT, 0) | ENABLE_VIRTUAL_TERMINAL_PROCESSING;
			SetConsoleMode.invoke(stderr, updated);
		}
	}
}