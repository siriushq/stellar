package org.apache.log4j.spi;

import org.apache.log4j.Logger;

/**
 * Shadow class for {@code org.apache.log4j.spi.LoggerFactory}.
 *
 * @author Mechite
 * @since 1.0
 */
public interface LoggerFactory {
	Logger makeNewLoggerInstance(String name);
}