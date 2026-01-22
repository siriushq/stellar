package sirius.stellar.ansicsi;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;

import static sirius.stellar.ansicsi.Terminal.ESCAPE;

/// Enumeration of control sequences that can manipulate a terminal.
///
/// These are exposed as public API on the namespace of [Terminal], and either
/// are [String]-returning constants, or methods that write to [PrintStream]s.
interface TerminalManipulation {

	/// Resets/reinitializes the entire terminal, usually useful if advanced
	/// escape sequences render it unusable.
	String HARD_RESET = ESCAPE + "c";

	/// Singleton instance used to evaluate [TerminalManipulation] constants
	/// that accept arguments, or related ones, scoping them for use exclusively
	/// against [Appendable]s (e.g. [PrintStream], [StringBuilder], etc.).
	Manipulate MANIPULATE = new Manipulate();
}

/// This class provides a context for control sequences that are not pure
/// constants, and accept arguments, as well as related constants that are
/// encapsulated & forced to be used against [Appendable]s.
final class Manipulate {

	/// Print the provided [CharSequence] to the provided appendable.
	private void print(Appendable appendable, CharSequence sequence) {
		try {
			appendable.append(sequence);
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}

	/// Move the cursor up one line.
	public void upLine(Appendable appendable) {
		this.print(appendable, ESCAPE + "A");
	}
	/// Move the cursor up `lines` times.
	public void upLines(Appendable appendable, int lines) {
		this.print(appendable, ESCAPE + lines + "A");
	}
	/// Move the cursor down one line.
	public void downLine(Appendable appendable) {
		this.print(appendable, ESCAPE + "B");
	}
	/// Move the cursor down `lines` times.
	public void downLines(Appendable appendable, int lines) {
		this.print(appendable, ESCAPE + lines + "B");
	}

	/// Move the cursor forward one character.
	public void forwardCharacter(Appendable appendable) {
		this.print(appendable, ESCAPE + "C");
	}
	/// Move the cursor forward `characters` times.
	public void forwardCharacters(Appendable appendable, int characters) {
		this.print(appendable, ESCAPE + characters + "C");
	}
	/// Move the cursor backward one character.
	public void backwardCharacter(Appendable appendable) {
		this.print(appendable, ESCAPE + "D");
	}
	/// Move the cursor backward `characters` times.
	public void backwardCharacters(Appendable appendable, int characters) {
		this.print(appendable, ESCAPE + characters + "D");
	}

	/// Move the cursor to the beginning of the next line.
	public void nextLine(Appendable appendable) {
		this.print(appendable, ESCAPE + "E");
	}
	/// Move the cursor to the beginning of the line that is `lines` times down.
	public void nextLines(Appendable appendable, int lines) {
		this.print(appendable, ESCAPE + lines + "E");
	}
	/// Move the cursor to the beginning of the previous line.
	public void previousLine(Appendable appendable) {
		this.print(appendable, ESCAPE + "F");
	}
	/// Move the cursor to the beginning of the line that is `lines` times up.
	public void previousLines(Appendable appendable, int lines) {
		this.print(appendable, ESCAPE + lines + "F");
	}

	/// Move cursor to specific position.
	public void position(Appendable appendable, int row, int column) {
		this.print(appendable, ESCAPE + row + ";" + column + "H");
	}

	/// Resize the screen (xterm-specific).
	public void resize(Appendable appendable, int row, int column) {
		this.print(appendable, ESCAPE + "8;" + row + ";" + column + "t");
	}

	/// Clear the entire screen.
	public void clear(Appendable appendable) {
		this.print(appendable, ESCAPE + "2J");
	}
	/// Clear the entire screen and delete scrollback.
	public void clearHistory(Appendable appendable) {
		this.print(appendable, ESCAPE + "3J");
	}
	/// Clear from cursor to beginning of screen.
	public void clearToBeginning(Appendable appendable) {
		this.print(appendable, ESCAPE + "1J");
	}
	/// Clear from cursor to end of screen.
	public void clearToEnd(Appendable appendable) {
		this.print(appendable, ESCAPE + "0J");
	}
	/// Clear current line.
	public void clearLine(Appendable appendable) {
		this.print(appendable, ESCAPE + "2K");
	}
	/// Clear from cursor to beginning of line.
	public void clearToLineBeginning(Appendable appendable) {
		this.print(appendable, ESCAPE + "1K");
	}
	/// Clear from cursor to end of line.
	public void clearToLineEnd(Appendable appendable) {
		this.print(appendable, ESCAPE + "0K");
	}

	/// Hide the cursor.
	public void hideCursor(Appendable appendable) {
		this.print(appendable, ESCAPE + "?25l");
	}
	/// Show the cursor.
	public void showCursor(Appendable appendable) {
		this.print(appendable, ESCAPE + "?25h");
	}

	/// Scroll up one line.
	public void scrollUp(Appendable appendable) {
		this.print(appendable, ESCAPE + "S");
	}
	/// Scroll up `lines` times.
	public void scrollUpLines(Appendable appendable, int lines) {
		this.print(appendable, ESCAPE + lines + "S");
	}
	/// Scroll down one line.
	public void scrollDown(Appendable appendable) {
		this.print(appendable, ESCAPE + "T");
	}
	/// Scroll down `lines` times.
	public void scrollDownLines(Appendable appendable, int lines) {
		this.print(appendable, ESCAPE + lines + "T");
	}

	/// Save the current cursor position.
	public void saveCursorPosition(Appendable appendable) {
		this.print(appendable, ESCAPE + "s");
	}
	/// Restore saved cursor position.
	public void restoreCursorPosition(Appendable appendable) {
		this.print(appendable, ESCAPE + "u");
	}
}