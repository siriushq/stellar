package sirius.stellar.ansicsi;

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
/// of customization to the output colors, e.g. `BLACK.foreground().bright()`.
///
/// ### Manipulation
/// Access to
///
/// @since 1.0
public final class Terminal
	implements TerminalColor, TerminalManipulation, TerminalStyle {

	/// Represents the CSI used to initiate ANSI escape sequences.
	public static final String ESCAPE = "\u001B[";

	/// Represents a CSI terminator for SGR (Select Graphic Rendition) commands.
	public static final String GRAPHIC = "m";

	static {
		try {
			ServiceLoader<TerminalExtension> loader = ServiceLoader.load(TerminalExtension.class);
			for (TerminalExtension extension : loader) extension.wire();
		} catch (Throwable throwable) {
			throw new IllegalStateException("Failed to wire ANSI CSI terminal extensions", throwable);
		}
	}


}