package org.apache.log4j;

import java.util.Hashtable;

/// Shadow class for `org.apache.log4j.MDC`.
///
/// @author Mechite
/// @since 1.0
public final class MDC {

	private static final Hashtable<String, Object> mdc = new Hashtable<>();

	public static Hashtable getContext() {
		return mdc;
	}

	public static Object get(String key) {
		return mdc.get(key);
	}

	public static void put(String key, Object value) {
		mdc.put(key, value);
	}

	public static void remove(String key) {
		mdc.remove(key);
	}

	public static void clear() {
		mdc.clear();
	}
}