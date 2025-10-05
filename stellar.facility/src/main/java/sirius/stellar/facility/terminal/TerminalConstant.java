package sirius.stellar.facility.terminal;

import sirius.stellar.facility.Strings;

/// Represents an ANSI escape code that has an effect in a terminal environment.
///
/// This can be used with the [TerminalConstant#toString] method inside any given
/// string that is intended to be output to a terminal. For example, it could be used
/// with the [Strings#format] method if retrieved from [TerminalColor] as follows
/// (where `...TerminalColor.*` is statically imported):
/// ```
/// Strings.format(
///     "{2}Here are some colors, {0}red {2}and {1}blue{2}.",
///     RED.foreground().bright(),
///     BLUE.foreground().bright(),
///     DEFAULT.foreground().bright()
/// );
/// ```
///
/// @since 1.0
/// @author Mechite
public final class TerminalConstant {

	private final String code;

	TerminalConstant(int code) {
		this(Integer.toString(code));
	}

	TerminalConstant(String code) {
		this.code = code;
	}

	/// Returns a string representation of the constant.
	///
	/// Ideally, this should be implicitly called where possible as ASCII constants
	/// are usually used for console messages and such - where concatenation implicitly
	/// calls this method through [String#valueOf(Object)].
	///
	/// @since 1.0
	@Override
	public String toString() {
		return "\u001B[" + this.code + "m";
	}
}