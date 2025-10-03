package sirius.stellar.logging.dispatch.slf4j;

/**
 * Implementation of {@link org.slf4j.ILoggerFactory} used for obtaining instances of {@link Slf4jDispatcher}.
 *
 * @since 1.0
 * @author Mechite
 */
public final class Slf4jFactory implements org.slf4j.ILoggerFactory {

	@Override
	public org.slf4j.Logger getLogger(String name) {
		return new Slf4jDispatcher(name);
	}
}
