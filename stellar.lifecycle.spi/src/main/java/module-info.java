module sirius.stellar.lifecycle.spi {

	requires static io.avaje.prism;
	requires static java.compiler;
	requires static org.jspecify;

	exports sirius.stellar.lifecycle.spi;

	provides javax.annotation.processing.Processor
		with sirius.stellar.lifecycle.spi.ServiceProcessor;
}