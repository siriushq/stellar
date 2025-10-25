import org.jspecify.annotations.NullMarked;
import sirius.stellar.logging.dispatch.Dispatcher;

@NullMarked
module sirius.stellar.logging.jul {

	requires org.jspecify;
	requires sirius.stellar.facility;
	requires sirius.stellar.logging;

	requires java.logging;

	exports sirius.stellar.logging.dispatch.jul;

	provides Dispatcher.Provider with JulDispatcherFactory;
}