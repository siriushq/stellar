import org.jspecify.annotations.NullMarked;
import sirius.stellar.configuration.*;

@NullMarked
module sirius.stellar.configuration {
	requires org.jspecify;
	exports sirius.stellar.configuration;

	requires static sirius.stellar.lifecycle.spi;
	requires static jdk.unsupported;

	uses ConfigurationProvider;
	uses ConfigurationReloader;

	provides ConfigurationProvider with
			PropertiesConfigurationProvider,
			EnvironmentConfigurationProvider,
			SystemConfigurationProvider;

	provides ConfigurationReloader with
			SignalConfigurationReloader,
			PropertiesConfigurationReloader;
}