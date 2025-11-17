package sirius.stellar.logging.dispatch.minlog;

import sirius.stellar.facility.annotation.Internal;
import sirius.stellar.logging.dispatch.Dispatcher;

/// Implementation of [Dispatcher.Provider] used for obtaining instances of [MinlogDispatcher].
/// This is run with [java.util.ServiceLoader].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Internal
public final class MinlogDispatcherFactory implements Dispatcher.Provider {

	@Override
	public Dispatcher create() {
		return new MinlogDispatcher(this);
	}
}