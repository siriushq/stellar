import sirius.stellar.lifecycle.natives.NativesProcessor;

module sirius.stellar.lifecycle.natives {
	requires static io.avaje.prism;
	requires static java.compiler;
	requires static io.jstach.jstache;

	provides javax.annotation.processing.Processor with NativesProcessor;
}