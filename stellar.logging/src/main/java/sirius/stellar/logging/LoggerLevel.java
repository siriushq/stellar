package sirius.stellar.logging;

/// An enumerator that defines a set of standard logging levels that can be used to
/// control logging output. The levels are ordered and specified by ordered integers.
///
/// Essentially, [#severity()] returns an integer value for the severity of a given level.
/// The higher the number, the more severe the logging is. More levels could be added at
/// any point in the future, so when debugging, the level should be simply set to
/// [Integer#MAX_VALUE] to include all messages.
///
/// In production, the level should generally be set to [LoggerLevel#ERROR]`.severity()`.
///
/// [#ALL] and [#OFF] are levels marked with severities of [Integer#MIN_VALUE] and
/// [Integer#MAX_VALUE] respectively. They should never be logged out directly, and the
/// [#severity()] method never called for them (they serve to route dispatchers' levels).
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public enum LoggerLevel {

	OFF(-1, "Off"),

	INFORMATION(0, "Information"),
	WARNING(1, "Warning"),
	ERROR(2, "Error"),

	STACKTRACE(3, "Stacktrace"),
	DEBUGGING(4, "Debugging"),
	CONFIGURATION(5, "Configuration"),

	ALL(Integer.MAX_VALUE, "All");

	private final int severity;
	private final String display;

	LoggerLevel(int severity, String display) {
		this.severity = severity;
		this.display = display;
	}

	@Override
	public String toString() {
		return this.display;
	}

	/// Returns an integer value for the severity of the level.
	/// @see LoggerLevel
	public int severity() {
		return this.severity;
	}

	/// Returns a display name for the level.
	/// @see LoggerLevel
	public String display() {
		return this.display;
	}
}