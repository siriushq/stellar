import io.avaje.recordbuilder.RecordBuilder;
import org.jspecify.annotations.NullMarked;

@NullMarked
@RecordBuilder
module sirius.stellar.logging {
	requires org.jspecify;

	requires static io.avaje.recordbuilder;
	requires static sirius.stellar.annotation;

	requires sirius.stellar.facility;

	exports sirius.stellar.logging.collect;
	exports sirius.stellar.logging.dispatch;
	exports sirius.stellar.logging.supplier;
	exports sirius.stellar.logging;

	uses sirius.stellar.logging.dispatch.Dispatcher.Provider;
	uses sirius.stellar.logging.collect.Collector.Provider;
}