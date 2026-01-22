package sirius.stellar.ansicsi;

import java.io.IOException;
import java.io.UncheckedIOException;

import static sirius.stellar.ansicsi.Terminal.ESCAPE;

/// Enumeration of methods creating control sequences for terminal manipulation,
/// against any [Appendable] (returned by the [#appendable()] implementation).
///
/// @see Terminal#manipulate(Appendable)
public interface TerminalManipulatable {

	/// Returns the appendable which backs this manipulatable terminal.
	Appendable appendable();

	/// Print the provided [CharSequence] to the provided appendable.
	private void print(CharSequence sequence) {
		try {
			this.appendable().append(sequence);
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}

	/// Move the cursor up one line.
	default void upLine() {
		this.print(ESCAPE + "A");
	}
	/// Move the cursor up `lines` times.
	default void upLines(int lines) {
		this.print(ESCAPE + lines + "A");
	}
	/// Move the cursor down one line.
	default void downLine() {
		this.print(ESCAPE + "B");
	}
	/// Move the cursor down `lines` times.
	default void downLines(int lines) {
		this.print(ESCAPE + lines + "B");
	}

	/// Move the cursor forward one character.
	default void forwardCharacter() {
		this.print(ESCAPE + "C");
	}
	/// Move the cursor forward `characters` times.
	default void forwardCharacters(int characters) {
		this.print(ESCAPE + characters + "C");
	}
	/// Move the cursor backward one character.
	default void backwardCharacter() {
		this.print(ESCAPE + "D");
	}
	/// Move the cursor backward `characters` times.
	default void backwardCharacters(int characters) {
		this.print(ESCAPE + characters + "D");
	}

	/// Move the cursor to the beginning of the next line.
	default void nextLine() {
		this.print(ESCAPE + "E");
	}
	/// Move the cursor to the beginning of the line that is `lines` times down.
	default void nextLines(int lines) {
		this.print(ESCAPE + lines + "E");
	}
	/// Move the cursor to the beginning of the previous line.
	default void previousLine() {
		this.print(ESCAPE + "F");
	}
	/// Move the cursor to the beginning of the line that is `lines` times up.
	default void previousLines(int lines) {
		this.print(ESCAPE + lines + "F");
	}

	/// Move cursor to specific position.
	default void position(int row, int column) {
		this.print(ESCAPE + row + ";" + column + "H");
	}

	/// Resize the screen (xterm-specific).
	default void resize(int row, int column) {
		this.print(ESCAPE + "8;" + row + ";" + column + "t");
	}

	/// Clear the entire screen.
	default void clear() {
		this.print(ESCAPE + "2J");
	}
	/// Clear the entire screen and delete scrollback.
	default void clearHistory() {
		this.print(ESCAPE + "3J");
	}
	/// Clear from cursor to beginning of screen.
	default void clearToBeginning() {
		this.print(ESCAPE + "1J");
	}
	/// Clear from cursor to end of screen.
	default void clearToEnd() {
		this.print(ESCAPE + "0J");
	}
	/// Clear current line.
	default void clearLine() {
		this.print(ESCAPE + "2K");
	}
	/// Clear from cursor to beginning of line.
	default void clearToLineBeginning() {
		this.print(ESCAPE + "1K");
	}
	/// Clear from cursor to end of line.
	default void clearToLineEnd() {
		this.print(ESCAPE + "0K");
	}

	/// Hide the cursor.
	default void hideCursor() {
		this.print(ESCAPE + "?25l");
	}
	/// Show the cursor.
	default void showCursor() {
		this.print(ESCAPE + "?25h");
	}

	/// Scroll up one line.
	default void scrollUp() {
		this.print(ESCAPE + "S");
	}
	/// Scroll up `lines` times.
	default void scrollUpLines(int lines) {
		this.print(ESCAPE + lines + "S");
	}
	/// Scroll down one line.
	default void scrollDown() {
		this.print(ESCAPE + "T");
	}
	/// Scroll down `lines` times.
	default void scrollDownLines(int lines) {
		this.print(ESCAPE + lines + "T");
	}

	/// Save the current cursor position.
	default void saveCursorPosition() {
		this.print(ESCAPE + "s");
	}
	/// Restore saved cursor position.
	default void restoreCursorPosition() {
		this.print(ESCAPE + "u");
	}
}