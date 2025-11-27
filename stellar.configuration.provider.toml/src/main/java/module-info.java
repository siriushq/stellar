import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.configuration.toml {
	requires org.jspecify;
	requires sirius.stellar.configuration;

	requires static io.avaje.spi;
	requires org.tomlj;

	exports sirius.stellar.configuration.toml;

	provides sirius.stellar.configuration.ConfigurationProvider with
			sirius.stellar.configuration.toml.TomlConfigurationProvider;
}