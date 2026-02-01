import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.configuration.properties {
	requires org.jspecify;
	requires sirius.stellar.configuration;

	requires static io.avaje.spi;

	exports sirius.stellar.configuration.properties;

	provides sirius.stellar.configuration.ConfigurationProvider
		with sirius.stellar.configuration.properties.PropertiesConfigurationProvider;
	provides sirius.stellar.configuration.ConfigurationReloader
		with sirius.stellar.configuration.properties.PropertiesConfigurationReloader;
}