import org.jspecify.annotations.NullMarked;
import sirius.stellar.configuration.ConfigurationProvider;
import sirius.stellar.configuration.EnvironmentConfigurationProvider;
import sirius.stellar.configuration.PropertiesConfigurationProvider;
import sirius.stellar.configuration.SystemConfigurationProvider;

@NullMarked
module sirius.stellar.configuration {
	requires org.jspecify;
	exports sirius.stellar.configuration;

	requires static sirius.stellar.lifecycle.spi;

	uses ConfigurationProvider;
	provides ConfigurationProvider with
			PropertiesConfigurationProvider,
			EnvironmentConfigurationProvider,
			SystemConfigurationProvider;
}