import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.configuration {
	requires org.jspecify;
	exports sirius.stellar.configuration;

	requires static io.avaje.spi;
	requires static jdk.unsupported;

	opens sirius.stellar.configuration to sirius.stellar.configuration.testing;

	provides sirius.stellar.configuration.ConfigurationProvider with
			sirius.stellar.configuration.PropertiesConfigurationProvider,
			sirius.stellar.configuration.EnvironmentConfigurationProvider,
			sirius.stellar.configuration.SystemConfigurationProvider;

	provides sirius.stellar.configuration.ConfigurationReloader with
			sirius.stellar.configuration.SignalConfigurationReloader,
			sirius.stellar.configuration.PropertiesConfigurationReloader;

	uses sirius.stellar.configuration.ConfigurationProvider;
	uses sirius.stellar.configuration.ConfigurationReloader;
}