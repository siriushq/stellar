package sirius.stellar.ansicsi.windows;

import jnr.ffi.LibraryLoader;
import jnr.ffi.Pointer;
import jnr.ffi.byref.IntByReference;
import sirius.stellar.ansicsi.TerminalExtension;

import static java.util.Objects.requireNonNull;

/// Implementation of [TerminalExtension] which automatically sets the
/// `ENABLE_VIRTUAL_TERMINAL_PROCESSING` output mode on `stdout`/`stderr`
/// and the output codepage to CP65001 (UTF-8).
///
/// @implNote This source file contains the Java <23 JNR-FFI based
/// implementation, a Java >23 Panama FFM based implementation exists.
/// @see "/src/main/java23/"
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

	private final Kernel32 kernel32;

	public WindowsTerminalExtension() {
		this.kernel32 = LibraryLoader.create(Kernel32.class)
				.stdcall()
				.failImmediately()
				.load("kernel32");
	}

	@Override
	public void wire() {
		if (this.kernel32.SetConsoleOutputCP(CP_UTF8) == 0) fail();

		Pointer stdout = this.kernel32.GetStdHandle(STD_OUTPUT_HANDLE);
		Pointer stderr = this.kernel32.GetStdHandle(STD_ERROR_HANDLE);

		requireNonNull(stdout);
		requireNonNull(stderr);

		IntByReference found = new IntByReference();
		int updated;

		if (this.kernel32.GetConsoleMode(stdout, found) == 0) fail();
		updated = found.getValue() | ENABLE_VIRTUAL_TERMINAL_PROCESSING;
		if (this.kernel32.SetConsoleMode(stdout, updated) == 0) fail();

		if (this.kernel32.GetConsoleMode(stderr, found) == 0) fail();
		updated = found.getValue() | ENABLE_VIRTUAL_TERMINAL_PROCESSING;
		if (this.kernel32.SetConsoleMode(stderr, updated) == 0) fail();
	}

	/// Shortcut for throw an exception, for use only in [#wire].
	private static void fail() {
		throw new IllegalStateException("kernel32 invocation failed");
	}

	/// Header definition of the required portions of the `kernel32` library.
	interface Kernel32 {

		// https://learn.microsoft.com/windows/console/setconsoleoutputcp
		int SetConsoleOutputCP(int wCodePageID);

		// https://learn.microsoft.com/windows/console/getstdhandle
		Pointer GetStdHandle(int nStdHandle);

		// https://learn.microsoft.com/windows/console/getconsolemode
		int GetConsoleMode(Pointer hConsoleHandle, IntByReference lpMode);

		// https://learn.microsoft.com/windows/console/setconsolemode
		int SetConsoleMode(Pointer hConsoleHandle, int dwMode);
	}
}