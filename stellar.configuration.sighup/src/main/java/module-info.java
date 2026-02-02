import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.configuration.sighup {
	requires org.jspecify;
	requires sirius.stellar.configuration;

	requires static io.avaje.spi;
	requires static jdk.unsupported;

	exports sirius.stellar.configuration.sighup;

	provides sirius.stellar.configuration.ConfigurationReloader
		with sirius.stellar.configuration.sighup.SignalConfigurationReloader;
}