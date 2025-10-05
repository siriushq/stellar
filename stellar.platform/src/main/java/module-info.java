import org.jspecify.annotations.NullMarked;
import io.avaje.inject.InjectModule;
import sirius.stellar.platform.Platform;

@NullMarked
@InjectModule(provides = Platform.class)
module sirius.stellar.platform {

	requires transitive org.jetbrains.annotations;
	requires transitive org.jspecify;

	requires transitive sirius.stellar.facility;
	requires transitive sirius.stellar.logging;

	requires transitive io.avaje.inject;
	requires transitive io.avaje.jsonb;
	requires transitive io.avaje.jsonb.plugin;

	requires java.management;

	exports sirius.stellar.platform;

	provides io.avaje.inject.spi.InjectExtension with sirius.stellar.platform.PlatformModule;
	provides io.avaje.jsonb.spi.JsonbExtension with sirius.stellar.platform.PlatformJsonComponent;
}