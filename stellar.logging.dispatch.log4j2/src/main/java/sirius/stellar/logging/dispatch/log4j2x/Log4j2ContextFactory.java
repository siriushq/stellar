package sirius.stellar.logging.dispatch.log4j2x;

import java.net.URI;

/// Implementation of [org.apache.logging.log4j.spi.LoggerContextFactory] used for obtaining instances of [Log4j2Context].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class Log4j2ContextFactory implements org.apache.logging.log4j.spi.LoggerContextFactory {

	@Override
	public org.apache.logging.log4j.spi.LoggerContext getContext(String caller, ClassLoader loader, Object externalContext, boolean currentContext) {
		return new Log4j2Context(externalContext);
	}

	@Override
	public org.apache.logging.log4j.spi.LoggerContext getContext(String caller, ClassLoader loader, Object externalContext, boolean currentContext, URI configLocation, String name) {
		return new Log4j2Context(externalContext);
	}

	@Override
	public void removeContext(org.apache.logging.log4j.spi.LoggerContext context) {
		assert true;
	}
}