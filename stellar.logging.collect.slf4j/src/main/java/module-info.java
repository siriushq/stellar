import org.jspecify.annotations.NullMarked;
import sirius.stellar.logging.collect.Collector;
import sirius.stellar.logging.collect.slf4j.Slf4jCollectorProvider;

@NullMarked
module sirius.stellar.logging.collect.slf4j {

	requires org.jspecify;
	requires org.slf4j;

	requires sirius.stellar.logging;
	requires sirius.stellar.facility;

	exports sirius.stellar.logging.collect.slf4j;

	provides Collector.Provider with Slf4jCollectorProvider;
}