package sirius.stellar.serialization.msgpack;

import io.avaje.json.stream.JsonStream;
import io.avaje.jsonb.spi.JsonStreamFactory;

/// Implementation of [JsonStreamFactory] for the [MsgpackAdapter] instances to be
/// automatically created with default settings if no configuration is desired and the
/// `sirius.stellar.serialization.msgpack` module is found on the module path (or on
/// the classpath via `META-INF/services/io.avaje.jsonb.spi.JsonStreamFactory`).
///
/// @see MsgpackAdapter
/// @since 1.0
/// @author Mechite
public final class MsgpackAdapterFactory implements JsonStreamFactory {

	@Override
	public JsonStream create(boolean serializeNulls, boolean serializeEmpty, boolean failOnUnknown) {
		return new MsgpackAdapter(serializeNulls, serializeEmpty, failOnUnknown);
	}
}