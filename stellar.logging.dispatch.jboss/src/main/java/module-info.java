import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.jboss {
	requires org.jspecify;

	requires sirius.stellar.logging;
	requires org.jboss.logging;

	exports sirius.stellar.logging.dispatch.jboss;

	provides org.jboss.logging.LoggerProvider
		with sirius.stellar.logging.dispatch.jboss.JbossProvider;
}