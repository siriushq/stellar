package sirius.stellar.logging.dispatch.minlog;

import sirius.stellar.facility.doctation.Internal;
import sirius.stellar.logging.dispatch.Dispatcher;

/**
 * Implementation of {@link Dispatcher.Provider} used for obtaining instances of {@link MinlogDispatcher}.
 * This is run with {@link java.util.ServiceLoader}.
 *
 * @since 1.0
 * @author Mechite
 */
@Internal
public final class MinlogFactory implements Dispatcher.Provider {

	@Override
	public Dispatcher create() {
		return new MinlogDispatcher(this);
	}
}