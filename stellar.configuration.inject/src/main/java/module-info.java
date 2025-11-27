import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.configuration.inject {
	requires sirius.stellar.configuration;
	requires static io.avaje.spi;

	requires io.avaje.inject;

	provides io.avaje.inject.spi.InjectExtension
		with sirius.stellar.configuration.inject.DConfigPropertyPlugin;

	exports sirius.stellar.configuration.inject;
}