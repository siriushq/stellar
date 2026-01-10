import org.jspecify.annotations.NullMarked;
import sirius.stellar.logging.dispatch.Dispatcher;
import sirius.stellar.logging.dispatch.log4j.Log4jDispatcherFactory;

@NullMarked
module sirius.stellar.logging.log4j {
	requires org.jspecify;
	requires sirius.stellar.logging;

	requires ch.qos.reload4j;

	provides Dispatcher.Provider with Log4jDispatcherFactory;
}