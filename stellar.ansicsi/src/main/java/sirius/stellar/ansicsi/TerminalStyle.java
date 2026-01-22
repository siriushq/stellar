package sirius.stellar.ansicsi;

import static sirius.stellar.ansicsi.Terminal.ESCAPE;
import static sirius.stellar.ansicsi.Terminal.GRAPHIC;

/// Enumeration of font/formatting sequences that can be rendered in a terminal.
/// These constants are exposed as public API on the namespace of [Terminal].
///
/// - [#RESET] (widely supported):
///     Resets/clears all styles and colors.
///
/// - [#BOLD] (widely supported):
///     Makes text bolder or, generally, increased in intensity.
///     Implementation of this style depends on the platform.
///
/// - [#FAINT] (not very widely supported):
///     Makes text fainter or, generally, decreased in intensity. Implementation
///     of and even support for this code varies depending on the platform.
///
/// - [#ITALIC] (not very widely supported):
///     Makes text italicized. On certain platforms this code can be
///     mistreated, instead as [#INVERSE] or [#BLINKING].
///
/// - [#UNDERLINE] (widely supported):
///     Makes text underlined.
///
/// - [#BLINKING] (widely supported):
///     Makes the text blink slowly (&lt;150 times per minute).
///
/// - [#RAPID_BLINKING] (not widely supported):
///     Makes the text blink rapidly (>150 times per minute).
///     MS-DOS `ANSI.SYS` is the reference implementation for this code.
///     This may be misinterpreted on other platforms.
///
/// - [#INVERSE] (widely supported):
///     Reverse video (or invert video, inverse video, reverse screen),
///     setting the background color to the foreground (text) color,
///     vice versa, i.e., swapping them around for use as a visual indicator.
///     [Wikipedia](https://en.wikipedia.org/wiki/Reverse_video)
///
/// - [#CONCEALED] (not very widely supported):
///     Conceals text (e.g., used for password inputs).
///     Support for this code varies depending on the platform.
///
/// - [#STRIKETHROUGH] (widely supported):
///     Makes the text struck through/crossed out.
///
/// - [#DOUBLE_UNDERLINE] (not very widely supported):
///     Double-underline (ECMA-48) but instead is commonly misinterpreted to
///     disable bold intensity on several other platforms, such as Linux \<4.17.
///
/// - [#OVERLINE] (widely supported):
///     Makes text over-lined.
///
/// @see Terminal
interface TerminalStyle {

	String
	RESET = ESCAPE + 0 + GRAPHIC,
	BOLD = ESCAPE + 1 + GRAPHIC,
	FAINT = ESCAPE + 2 + GRAPHIC,
	ITALIC = ESCAPE + 3 + GRAPHIC,
	UNDERLINE = ESCAPE + 4 + GRAPHIC,
	BLINKING = ESCAPE + 5 + GRAPHIC,
	RAPID_BLINKING = ESCAPE + 6 + GRAPHIC,
	INVERSE = ESCAPE + 7 + GRAPHIC,
	CONCEALED = ESCAPE + 8 + GRAPHIC,
	STRIKETHROUGH = ESCAPE + 9 + GRAPHIC,
	DOUBLE_UNDERLINE = ESCAPE + 21 + GRAPHIC,
	OVERLINE = ESCAPE + 53 + GRAPHIC;
}