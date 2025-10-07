package sirius.stellar.logging.dispatch.slf4j;

/// Implementation of [org.slf4j.ILoggerFactory] used for obtaining instances of [Slf4jDispatcher].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class Slf4jFactory implements org.slf4j.ILoggerFactory {

	@Override
	public org.slf4j.Logger getLogger(String name) {
		return new Slf4jDispatcher(name);
	}
}
