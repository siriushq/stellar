package org.apache.log4j;

/// Shadow class for `org.apache.log4j.Priority`.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public class Priority {

	public static final int OFF_INT = Integer.MAX_VALUE;
	public static final int FATAL_INT = 50000;
	public static final int ERROR_INT = 40000;
	public static final int WARN_INT = 30000;
	public static final int INFO_INT = 20000;
	public static final int DEBUG_INT = 10000;
	public static final int ALL_INT = Integer.MIN_VALUE;

	public static final Priority FATAL = LevelHolder.FATAL;
	public static final Priority ERROR = LevelHolder.ERROR;
	public static final Priority WARN = LevelHolder.WARN;
	public static final Priority INFO = LevelHolder.INFO;
	public static final Priority DEBUG = LevelHolder.DEBUG;

	transient int level;
	transient String levelStr;
	transient int syslogEquivalent;

	protected Priority() {
		level = DEBUG.level;
		levelStr = DEBUG.levelStr;
		syslogEquivalent = DEBUG.syslogEquivalent;
	}

	protected Priority(final int level, final String levelStr, final int syslogEquivalent) {
		this.level = level;
		this.levelStr = levelStr;
		this.syslogEquivalent = syslogEquivalent;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Priority priority) return this.level == priority.level;
		return false;
	}

	@Override
	public int hashCode() {
		return this.level;
	}

	public final int getSyslogEquivalent() {
		return syslogEquivalent;
	}

	public boolean isGreaterOrEqual(final Priority r) {
		return level >= r.level;
	}

	public static Priority[] getAllPossiblePriorities() {
		return new Priority[] { Priority.FATAL, Priority.ERROR, Level.WARN,
			Priority.INFO, Priority.DEBUG };
	}

	@Override
	public final String toString() {
		return levelStr;
	}

	public final int toInt() {
		return level;
	}

	public static Priority toPriority(final String sArg) {
		return Level.toLevel(sArg);
	}

	public static Priority toPriority(final int val) {
		return toPriority(val, Priority.DEBUG);
	}

	public static Priority toPriority(final int val, final Priority defaultPriority) {
		return Level.toLevel(val, (Level) defaultPriority);
	}

	public static Priority toPriority(final String sArg, final Priority defaultPriority) {
		return Level.toLevel(sArg, (Level) defaultPriority);
	}

	private static class LevelHolder {
		private static final Priority FATAL = Level.FATAL;
		private static final Priority ERROR = Level.ERROR;
		private static final Priority WARN = Level.WARN;
		private static final Priority INFO = Level.INFO;
		private static final Priority DEBUG = Level.DEBUG;
	}
}