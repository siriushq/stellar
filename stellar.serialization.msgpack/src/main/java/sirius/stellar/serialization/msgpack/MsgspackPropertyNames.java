package sirius.stellar.serialization.msgpack;

import io.avaje.json.PropertyNames;
import org.msgpack.core.annotations.Nullable;

import java.util.Arrays;

/// Implementation of [PropertyNames] for MessagePack.
record MsgspackPropertyNames(String... names) implements PropertyNames {

	@Nullable
	public String get(String name) {
		return Arrays.stream(this.names)
				.filter(found -> found.equals(name))
				.findFirst()
				.orElse(null);
	}
}