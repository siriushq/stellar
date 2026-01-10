package sirius.stellar.logging.dispatch.log4j;

import sirius.stellar.logging.dispatch.Dispatcher;

/// Implementation of [Dispatcher.Provider] used for obtaining instances of [Log4jDispatcher].
/// This is run with [java.util.ServiceLoader].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class Log4jDispatcherFactory implements Dispatcher.Provider {

	@Override
	public Dispatcher create() {
		return new Log4jDispatcher();
	}
}