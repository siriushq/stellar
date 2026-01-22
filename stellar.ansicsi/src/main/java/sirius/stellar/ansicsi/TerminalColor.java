package sirius.stellar.ansicsi;

import static sirius.stellar.ansicsi.Terminal.ESCAPE;

/// Enumeration of color code sequences that can be rendered in a terminal.
/// These constants are exposed as public API on the namespace of [Terminal].
///
/// [#BLACK]:
///     <b style="color: #000000">[Dark][Relational#dark()]</b>,
///     <b style="color: #565758">[Bright][Relational#bright()]</b> \
/// [#RED]:
///     <b style="color: #E5504F">[Dark][Relational#dark()]</b>,
///     <b style="color: #F54152">[Bright][Relational#bright()]</b> \
/// [#GREEN]:
///     <b style="color: #58912F">[Dark][Relational#dark()]</b>,
///     <b style="color: #4DBD19">[Bright][Relational#bright()]</b> \
/// [#YELLOW]:
///     <b style="color: #A08712">[Dark][Relational#dark()]</b>,
///     <b style="color: #DDBC0A">[Bright][Relational#bright()]</b> \
/// [#BLUE]:
///     <b style="color: #3890D0">[Dark][Relational#dark()]</b>,
///     <b style="color: #24B2FD">[Bright][Relational#bright()]</b> \
/// [#MAGENTA]:
///     <b style="color: #A374C0">[Dark][Relational#dark()]</b>,
///     <b style="color: #E57FEB">[Bright][Relational#bright()]</b> \
/// [#CYAN]:
///     <b style="color: #08A6A7">[Dark][Relational#dark()]</b>,
///     <b style="color: #02DCDE">[Bright][Relational#bright()]</b> \
/// [#WHITE]:
///     <b style="color: #7B7C7E">[Dark][Relational#dark()]</b>,
///     <b style="color: #F5F6F7">[Bright][Relational#bright()]</b> \
/// [#DEFAULT]:
///     This represents the default color for the running terminal.
///     Currently, this is the only [Isoluminant] color available.
///
/// @see Terminal
public interface TerminalColor {

	Anisoluminant
	BLACK = new Anisoluminant(30, 90, 40, 100),
	RED = new Anisoluminant(31, 91, 41, 101),
	GREEN = new Anisoluminant(32, 92, 42, 102),
	YELLOW = new Anisoluminant(33, 93, 43, 103),
	BLUE = new Anisoluminant(34, 94, 44, 104),
	MAGENTA = new Anisoluminant(35, 95, 45, 105),
	CYAN = new Anisoluminant(36, 96, 46, 106),
	WHITE = new Anisoluminant(37, 97, 47, 107);

	Isoluminant
	DEFAULT = new Isoluminant(39, 49);

	/// Represents a color with separate dark and bright variants.
	final class Anisoluminant {

		private final Relational foreground;
		private final Relational background;

		Anisoluminant(int foregroundDark, int foregroundBright,
				int backgroundDark, int backgroundBright) {
			this.foreground = new Relational(foregroundDark, foregroundBright);
			this.background = new Relational(backgroundDark, backgroundBright);
		}

		/// Returns the foreground variant of this [Anisoluminant] color.
		/// @since 1.0
		public Relational foreground() {
			return this.foreground;
		}

		/// Returns the background variant of this [Anisoluminant] color.
		/// @since 1.0
		public Relational background() {
			return this.background;
		}
	}

	/// Represents a selected foreground or background state of an
	/// [Anisoluminant] color, allowing for access to dark or bright forms.
	final class Relational {

		private final int dark;
		private final int bright;

		Relational(int dark, int bright) {
			this.dark = dark;
			this.bright = bright;
		}

		/// Returns the dark form, for a given state of [Anisoluminant] color.
		/// @since 1.0
		public String dark() {
			return ESCAPE + this.dark + "m";
		}

		/// Returns the bright form, for a given state of [Anisoluminant] color.
		/// @since 1.0
		public String bright() {
			return ESCAPE + this.bright + "m";
		}
	}

	/// Represents a color without dark or bright variants, only positioning.
	final class Isoluminant {

		private final int foreground;
		private final int background;

		Isoluminant(int foreground, int background) {
			this.foreground = foreground;
			this.background = background;
		}

		/// Returns the foreground variant of this [Isoluminant] color.
		/// @since 1.0
		public String foreground() {
			return ESCAPE + this.foreground + "m";
		}

		/// Returns the background variant of this [Isoluminant] color.
		/// @since 1.0
		public String background() {
			return ESCAPE + this.background + "m";
		}
	}
}