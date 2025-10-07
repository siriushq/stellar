package org.apache.log4j;

import java.io.*;
import java.util.Locale;

/// Shadow class for `org.apache.log4j.Level`.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public class Level extends Priority implements Serializable {

	@Serial
	private static final long serialVersionUID = 3491141966387921974L;

	public static final int TRACE_INT = 5000;

	public static final Level OFF = new Level(OFF_INT, "OFF", 0);
	public static final Level FATAL = new Level(FATAL_INT, "FATAL", 0);
	public static final Level ERROR = new Level(ERROR_INT, "ERROR", 3);
	public static final Level WARN = new Level(WARN_INT, "WARN", 4);
	public static final Level INFO = new Level(INFO_INT, "INFO", 6);
	public static final Level DEBUG = new Level(DEBUG_INT, "DEBUG", 7);
	public static final Level TRACE = new Level(TRACE_INT, "TRACE", 7);
	public static final Level ALL = new Level(ALL_INT, "ALL", 7);

	protected Level(final int level, final String levelString, final int syslogEquivalent) {
		super(level, levelString, syslogEquivalent);
	}

	public static Level toLevel(final String string) {
		return toLevel(string, Level.DEBUG);
	}

	public static Level toLevel(final int value) {
		return toLevel(value, Level.DEBUG);
	}

	public static Level toLevel(final int value, final Level defaultLevel) {
		return switch (value) {
			case ALL_INT -> ALL;
			case DEBUG_INT -> Level.DEBUG;
			case INFO_INT -> Level.INFO;
			case WARN_INT -> Level.WARN;
			case ERROR_INT -> Level.ERROR;
			case FATAL_INT -> Level.FATAL;
			case OFF_INT -> OFF;
			case TRACE_INT -> Level.TRACE;
			default -> defaultLevel;
		};
	}

	public static Level toLevel(String string, Level defaultLevel) {
		if (string == null) return defaultLevel;
		return switch (string.toUpperCase(Locale.ROOT)) {
			case "ALL" -> Level.ALL;
			case "DEBUG" -> Level.DEBUG;
			case "INFO" -> Level.INFO;
			case "WARN" -> Level.WARN;
			case "ERROR" -> Level.ERROR;
			case "FATAL" -> Level.FATAL;
			case "OFF" -> Level.OFF;
			case "TRACE" -> Level.TRACE;
			default -> defaultLevel;
		};
	}

	@Serial
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		level = stream.readInt();
		syslogEquivalent = stream.readInt();
		levelStr = stream.readUTF();
	}

	@Serial
	private void writeObject(final ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();
		stream.writeInt(level);
		stream.writeInt(syslogEquivalent);
		stream.writeUTF(levelStr);
	}

	@Serial
	protected Object readResolve() throws ObjectStreamException {
		if (getClass() == Level.class) return toLevel(level);
		return this;
	}
}