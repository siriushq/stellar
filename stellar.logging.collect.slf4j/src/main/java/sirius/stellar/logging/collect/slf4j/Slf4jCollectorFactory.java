package sirius.stellar.logging.collect.slf4j;

import sirius.stellar.facility.doctation.Internal;
import sirius.stellar.logging.collect.Collector;

/// Implementation of [Collector.Provider] used for obtaining instances of [Slf4jCollector].
/// This is run with [java.util.ServiceLoader].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Internal
public final class Slf4jCollectorFactory implements Collector.Provider {

	@Override
	public Collector create() {
		return new Slf4jCollector();
	}
}