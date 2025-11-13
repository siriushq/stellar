import org.jspecify.annotations.NullMarked;
import sirius.stellar.configuration.inject.DConfigPropertyPlugin;

@NullMarked
module sirius.stellar.configuration.inject {
	requires sirius.stellar.configuration;
	requires static sirius.stellar.lifecycle.spi;

	requires io.avaje.inject;
	provides io.avaje.inject.spi.ConfigPropertyPlugin with DConfigPropertyPlugin;

	exports sirius.stellar.configuration.inject;
}