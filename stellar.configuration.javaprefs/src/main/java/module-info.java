import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.configuration.javaprefs {
	requires org.jspecify;
	requires sirius.stellar.configuration;

	requires static io.avaje.spi;
	requires java.prefs;

	exports sirius.stellar.configuration.javaprefs;

	provides sirius.stellar.configuration.ConfigurationProvider with
			sirius.stellar.configuration.javaprefs.JavaprefsConfiguration;
	provides sirius.stellar.configuration.ConfigurationReloader with
			sirius.stellar.configuration.javaprefs.JavaprefsConfiguration;
}