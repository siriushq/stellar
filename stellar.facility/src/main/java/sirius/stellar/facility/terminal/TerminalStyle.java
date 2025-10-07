package sirius.stellar.facility.terminal;

/// An enumerator for ANSI escape codes that specify text styles and formatting.
///
/// These codes are used to control styling and formatting of text output to terminal
/// or console environments that support ANSI escape sequences.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public enum TerminalStyle {

	/// Resets/clears all styles and colors.
	/// This is widely supported.
	RESET(0),

	/// Makes text bolder or, generally, increased in intensity.
	/// Implementation of this style depends on the platform.
	/// This is widely supported.
	BOLD(1),

	/// Makes text fainter or, generally, decreased in intensity.
	/// Implementation of and even support for this code varies depending on the platform.
	/// It is not very widely supported.
	FAINT(2),

	/// Makes text italicized.
	/// On certain platforms this code is treated as [#INVERSE] or [#BLINKING].
	/// It is not very widely supported.
	ITALIC(3),

	/// Makes the text underlined.
	/// This is widely supported.
	UNDERLINE(4),

	/// Makes the text blink slowly (&lt;150 times per minute).
	/// This is widely supported.
	///
	/// @see #RAPID_BLINKING
	BLINKING(5),

	/// Makes the text blink rapidly (>150 times per minute).
	/// MS-DOS ANSI-SYS is the reference implementation for this code.
	/// It is not widely supported and has many other meanings on other platforms.
	///
	/// @see #BLINKING
	RAPID_BLINKING(6),

	/// Reverse video (or invert video or inverse video or reverse screen).
	///
	/// Sets the background color to the foreground (text) color, and the background
	/// color to the foreground color, i.e., swapping them around for use as a visual
	/// indicator.
	///
	/// @see <a href="https://en.wikipedia.org/wiki/Reverse_video">on Wikipedia</a>
	INVERSE(7),

	/// Conceals text (e.g., used for password inputs).
	/// Support for this code varies depending on the platform.
	/// It is not very widely supported.
	CONCEALED(8),

	/// Makes the text struck through/crossed out.
	/// This is widely supported.
	STRIKETHROUGH(9),

	/// Double-underline (ECMA-48) but instead disables bold intensity on several
	/// other platforms such as Linux &lt;4.17. Therefore, this is not widely supported.
	DOUBLE_UNDERLINE(21),

	/// Makes text over-lined.
	/// This is widely supported.
	OVERLINE(53);

	private final int code;

	TerminalStyle(int code) {
		this.code = code;
	}

    /// Gets the constant escape code value of this terminal style.
    /// This can then be output to any supported environment using [TerminalConstant#toString()].
    /// However, [TerminalStyle#toString()] should be preferred.
    ///
    /// @see TerminalConstant
    /// @since 1.0
    public TerminalConstant get() {
		return new TerminalConstant(this.code);
	}

	@Override
	public String toString() {
		return this.get().toString();
	}
}