package sirius.stellar.logging.dispatch.slf4j;

/// Implementation of [org.slf4j.spi.SLF4JServiceProvider] used for obtaining instances of [Slf4jDispatcherFactory].
///
/// @version SLF4J 2.0.7 (or any other binary compatible variant).
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class Slf4jServiceProvider implements org.slf4j.spi.SLF4JServiceProvider {

	private org.slf4j.ILoggerFactory loggerFactory;
	private org.slf4j.IMarkerFactory markerFactory;
	private org.slf4j.spi.MDCAdapter mdcAdapter;

	@Override
	public org.slf4j.ILoggerFactory getLoggerFactory() {
		return this.loggerFactory;
	}

	@Override
	public org.slf4j.IMarkerFactory getMarkerFactory() {
		return this.markerFactory;
	}

	@Override
	public org.slf4j.spi.MDCAdapter getMDCAdapter() {
		return this.mdcAdapter;
	}

	@Override
	public String getRequestedApiVersion() {
		return "2.0.7";
	}

	@Override
	public void initialize() {
		this.loggerFactory = new Slf4jDispatcherFactory();
		this.markerFactory = new org.slf4j.helpers.BasicMarkerFactory();
		this.mdcAdapter = new org.slf4j.helpers.BasicMDCAdapter();
	}
}
