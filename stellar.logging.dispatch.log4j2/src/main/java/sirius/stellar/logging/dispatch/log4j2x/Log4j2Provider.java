package sirius.stellar.logging.dispatch.log4j2x;

/// Implementation of [org.apache.logging.log4j.spi.Provider] providing instances of [Log4j2ContextFactory].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class Log4j2Provider extends org.apache.logging.log4j.spi.Provider {

	public Log4j2Provider() {
		super(0, CURRENT_VERSION);
	}

	@Override
	public org.apache.logging.log4j.spi.LoggerContextFactory getLoggerContextFactory() {
		return new Log4j2ContextFactory();
	}
}