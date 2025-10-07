package sirius.stellar.logging.dispatch.jul;

import sirius.stellar.facility.doctation.Internal;
import sirius.stellar.logging.dispatch.Dispatcher;

/// Implementation of [Dispatcher.Provider] used for obtaining instances of [JulDispatcher].
/// This is run with [java.util.ServiceLoader].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Internal
public final class JulFactory implements Dispatcher.Provider {

	@Override
	public Dispatcher create() {
		return new JulDispatcher(this);
	}
}