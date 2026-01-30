import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.jcl {
	requires org.jspecify;

	requires sirius.stellar.logging;
	requires org.apache.commons.logging;

	exports sirius.stellar.logging.dispatch.jcl;

	provides org.apache.commons.logging.LogFactory
		with sirius.stellar.logging.dispatch.jcl.JclDispatcherFactory;
}