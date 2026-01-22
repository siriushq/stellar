package sirius.stellar.ansicsi;

import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ServiceLoader;

/// This class is the entry-point for exports of ANSI (American National
/// Standards Institute) standard CSI (Control Sequence Introducer) constants
/// & utilities related to their use.
///
/// The constants here are typically used via explicit static imports,
/// for example, `import static sirius.stellar.ansicsi.Terminal.ESCAPE;`.
///
/// ### Colors
/// Access to colors is available via "fluent" syntax to allow a large amount
/// of customization to the output colors, e.g. `BLACK.foreground().bright()`,
/// `println(GREEN.foreground().bright() + "Hello, world!");`, etc.
///
/// ### Styles
/// Access to font and/or formatting sequences is available as pure constants,
/// such as [#BOLD], e.g. `println(BOLD + "Hello, world!");`.
///
/// ### Manipulation
/// Access to any more advanced sequences for manipulating terminals is forced
/// to be purpose-specifically used against an [Appendable], via the static
/// factory method [#manipulate(Appendable)].
///
/// @since 1.0
public final class Terminal
	implements TerminalColor, TerminalStyle {

	/// Represents the CSI used to initiate ANSI escape sequences.
	public static final String ESCAPE = "\u001B[";

	/// Represents a CSI terminator for SGR (Select Graphic Rendition) commands.
	public static final String GRAPHIC = "m";

	/// Represents a CSI used to reset/clear all styles and colors.
	public static final String CLEAR = ESCAPE + 0 + GRAPHIC;

	/// Represents a CSI used to reset/reinitialize the entire terminal,
	/// usually useful if advanced escape sequences render it unusable.
	public static final String RESET = ESCAPE + "c";

	static {
		try {
			ServiceLoader<TerminalExtension> loader = ServiceLoader.load(TerminalExtension.class);
			for (TerminalExtension extension : loader) extension.wire();
		} catch (Throwable throwable) {
			throw new IllegalStateException("Failed to wire ANSI CSI terminal extensions", throwable);
		}
	}

	/// Private constructor of [Terminal], which should never be instantiated.
	private Terminal() {
		throw new AssertionError();
	}

	/// Creates a [TerminalManipulatable] that treats the provided [Appendable]
	/// as a terminal, which can be manipulated by appending CSIs to it.
	///
	/// This is usually used with a [PrintStream] (such as [System#out]),
	/// or a [StringBuilder] or [StringWriter] for saving/buffering the created
	/// CSIs for later use against a terminal.
	///
	/// If generated CSIs are desired as pure [String]s, a [StringBuilder]
	/// with initial size of `4` characters is the most suitable candidate.
	public static TerminalManipulatable manipulate(Appendable appendable) {
		return () -> appendable;
	}
}