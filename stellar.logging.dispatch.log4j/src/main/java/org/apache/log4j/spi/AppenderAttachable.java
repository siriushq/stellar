package org.apache.log4j.spi;

import java.util.Enumeration;

import org.apache.log4j.Appender;

/**
 * Shadow class for {@code org.apache.log4j.spi.AppenderAttachable}.
 *
 * @author Mechite
 * @since 1.0
 */
public interface AppenderAttachable {
	void addAppender(Appender newAppender);
	Enumeration getAllAppenders();
	Appender getAppender(String name);
	boolean isAttached(Appender appender);
	void removeAllAppenders();
	void removeAppender(Appender appender);
	void removeAppender(String name);
}