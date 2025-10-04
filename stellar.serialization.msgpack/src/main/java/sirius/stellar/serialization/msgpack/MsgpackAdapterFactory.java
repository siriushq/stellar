package sirius.stellar.serialization.msgpack;

import io.avaje.json.stream.JsonStream;
import io.avaje.jsonb.spi.JsonStreamFactory;

/**
 * Implementation of {@link JsonStreamFactory} for the {@link MsgpackAdapter} instances to be
 * automatically created with default settings if no configuration is desired and the
 * {@code sirius.stellar.serialization.msgpack} module is found on the module path (or on
 * the classpath via {@code META-INF/services/io.avaje.jsonb.spi.JsonStreamFactory}).
 *
 * @see MsgpackAdapter
 * @since 1.0
 * @author Mechite
 */
public final class MsgpackAdapterFactory implements JsonStreamFactory {

	@Override
	public JsonStream create(boolean serializeNulls, boolean serializeEmpty, boolean failOnUnknown) {
		return new MsgpackAdapter(serializeNulls, serializeEmpty, failOnUnknown);
	}
}