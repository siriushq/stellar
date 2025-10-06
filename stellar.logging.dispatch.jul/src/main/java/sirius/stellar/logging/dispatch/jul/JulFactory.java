package sirius.stellar.logging.dispatch.jul;

import sirius.stellar.facility.doctation.Internal;
import sirius.stellar.logging.dispatch.Dispatcher;

/// Implementation of [Dispatcher.Provider] used for obtaining instances of [JulDispatcher].
/// This is run with [java.util.ServiceLoader].
///
/// @since 1.0
/// @author Mechite
@Internal
public final class JulFactory implements Dispatcher.Provider {

	@Override
	public Dispatcher create() {
		return new JulDispatcher(this);
	}
}