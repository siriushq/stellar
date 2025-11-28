import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.configuration.jshell {
	requires org.jspecify;
	requires sirius.stellar.configuration;

	requires static io.avaje.spi;
	requires jdk.jshell;

	exports sirius.stellar.configuration.jshell;

	provides sirius.stellar.configuration.ConfigurationProvider
			with sirius.stellar.configuration.jshell.JShellConfigurationProvider;
	provides sirius.stellar.configuration.ConfigurationReloader
			with sirius.stellar.configuration.jshell.JShellConfigurationReloader;
}