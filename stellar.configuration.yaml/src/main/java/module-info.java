import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.configuration.yaml {
	requires org.jspecify;

	requires sirius.stellar.configuration;
	requires sirius.stellar.configuration.file;

	requires static io.avaje.spi;
	requires org.yaml.snakeyaml;

	exports sirius.stellar.configuration.yaml;

	provides sirius.stellar.configuration.ConfigurationProvider with
			sirius.stellar.configuration.yaml.YamlConfigurationProvider;
	provides sirius.stellar.configuration.ConfigurationReloader with
			sirius.stellar.configuration.yaml.YamlConfigurationReloader;
}