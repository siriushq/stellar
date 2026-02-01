import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.configuration.testing {
	requires org.jspecify;

	requires sirius.stellar.configuration;
	requires static sirius.stellar.configuration.mutator;
	requires static sirius.stellar.lifecycle.testing;
	requires static io.avaje.spi;

	provides sirius.stellar.configuration.ConfigurationProvider
		with sirius.stellar.configuration.testing.TestConfigurationProvider;
}