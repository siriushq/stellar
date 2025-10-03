package sirius.stellar.logging.dispatch.log4j2x;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link org.apache.logging.log4j.spi.LoggerContext} used for obtaining instances of {@link Log4j2Dispatcher}.
 *
 * @since 1.0
 * @author Mechite
 */
public final class Log4j2Context implements org.apache.logging.log4j.spi.LoggerContext {

	private final List<org.apache.logging.log4j.spi.ExtendedLogger> loggers;
	private final Object externalContext;

	Log4j2Context(Object externalContext) {
		this.loggers = new ArrayList<>();
		this.externalContext = externalContext;
	}

	@Override
	public Object getExternalContext() {
		return this.externalContext;
	}

	@Override
	public org.apache.logging.log4j.spi.ExtendedLogger getLogger(String name) {
		Log4j2Dispatcher logger = new Log4j2Dispatcher(name);
		loggers.add(logger);
		return logger;
	}

	@Override
	public org.apache.logging.log4j.spi.ExtendedLogger getLogger(String name, org.apache.logging.log4j.message.MessageFactory factory) {
		Log4j2Dispatcher logger = new Log4j2Dispatcher(name, factory);
		loggers.add(logger);
		return logger;
	}

	@Override
	public boolean hasLogger(String name) {
		return this.loggers.stream().anyMatch(logger -> logger.getName().equalsIgnoreCase(name));
	}

	@Override
	public boolean hasLogger(String name, Class<? extends org.apache.logging.log4j.message.MessageFactory> messageFactoryClass) {
		return this.loggers.stream()
				.filter(logger -> logger.getName().equalsIgnoreCase(name))
				.anyMatch(logger -> logger.getMessageFactory().getClass().equals(messageFactoryClass));
	}

	@Override
	public boolean hasLogger(String name, org.apache.logging.log4j.message.MessageFactory messageFactory) {
		return this.loggers.stream()
				.filter(logger -> logger.getName().equalsIgnoreCase(name))
				.anyMatch(logger -> logger.getMessageFactory().equals(messageFactory));
	}
}