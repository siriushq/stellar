package org.apache.log4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.spi.LoggerFactory;

/// Shadow class for `org.apache.log4j.LogManager`.
///
/// @author Mechite
/// @since 1.0
public class LogManager {

	private static final Logger root = new Logger(null, "root");
	private static final Map<String, Logger> loggers = new HashMap<>();
	private static final Object mutex = new Object();

	static {
		loggers.put(root.getName(), root);
	}

	public static Logger getRootLogger() {
		return root;
	}

	public static Logger getLogger(final String name) {
		synchronized (mutex) {
			return getOrCreateLogger(name);
		}
	}

	public static Logger getLogger(final String name, final LoggerFactory factory) {
		return getLogger(name);
	}

	public static Logger getLogger(final Class clazz) {
		return getLogger(clazz.getName());
	}

	public static Logger exists(final String name) {
		synchronized (mutex) {
			return loggers.get(name);
		}
	}

	public static Enumeration getCurrentLoggers() {
		ArrayList<Logger> copy;
		synchronized (mutex) {
			copy = new ArrayList<Logger>(loggers.values());
		}
		copy.remove(root);
		return Collections.enumeration(copy);
	}

	public static void shutdown() {
		assert true;
	}

	public static void resetConfiguration() {
		assert true;
	}

	static Logger getParentLogger(String name) {
		return getLogger(reduce(name));
	}

	private static Logger getOrCreateLogger(String name) {
		if (name == null || name.isEmpty()) return root;
		Logger logger = loggers.get(name);
		if (logger == null) {
			Logger parent = getOrCreateLogger(reduce(name));
			logger = new Logger(parent, name);
			loggers.put(name, logger);
		}
		return logger;
	}

	private static String reduce(String name) {
		int index = name.lastIndexOf('.');
		return index == -1 ? null : name.substring(0, index);
	}
}