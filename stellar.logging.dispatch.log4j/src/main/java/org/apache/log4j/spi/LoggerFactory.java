package org.apache.log4j.spi;

import org.apache.log4j.Logger;

/// Shadow class for `org.apache.log4j.spi.LoggerFactory`.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public interface LoggerFactory {
	Logger makeNewLoggerInstance(String name);
}