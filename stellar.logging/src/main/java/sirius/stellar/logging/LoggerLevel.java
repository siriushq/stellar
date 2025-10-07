package sirius.stellar.logging;

/**
 * An enumerator that defines a set of standard logging levels that can be used to
 * control logging output. The levels are ordered and specified by ordered integers.
 * <p>
 * Essentially, {@link LoggerLevel#severity()} returns an integer value for the severity
 * of a given level. The higher the number, the more severe the logging is. More levels
 * could be added at any point in the future, so when debugging, the level should be
 * simply set to {@link Integer#MAX_VALUE} to include all messages. In production,
 * the level should be set to {@code 2}.
 * <p>
 * {@link LoggerLevel#ALL} and {@link LoggerLevel#OFF} are levels marked with severities
 * of {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE} respectively. They should
 * never be logged out directly, and the {@link LoggerLevel#severity()} method never
 * called for them (they serve for routing dispatchers' levels).
 *
 * @author Mahied Maruf (mechite)
 * @since 1.0
 */
public enum LoggerLevel {

	ALL(Integer.MIN_VALUE, "All"),

	INFORMATION(0, "Information"),
	WARNING(1, "Warning"),
	ERROR(2, "Error"),

	STACKTRACE(3, "Stacktrace"),
	DEBUGGING(4, "Debugging"),
	CONFIGURATION(5, "Configuration"),

	OFF(Integer.MAX_VALUE, "Off");

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

	/**
	 * Returns an integer value for the severity of the level.
	 * <p>
	 * More levels could be added at any point in the future, so when debugging, the level
	 * should be simply set to {@link Integer#MAX_VALUE} to include all messages. In production,
	 * the level should be set to {@code 2}.
	 */
	public int severity() {
		return this.severity;
	}

	/**
	 * Returns a display name for the level.
	 */
	public String display() {
		return this.display;
	}
}