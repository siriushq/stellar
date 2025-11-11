package sirius.stellar.serialization.msgpack.jsonb;

import io.avaje.jsonb.Jsonb;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sirius.stellar.facility.Strings;

import java.io.OutputStream;

import static org.assertj.core.api.Assertions.*;

final class MsgpackAdapterTest {

	@Test @DisplayName("adapter is located automagically by Jsonb with ServiceLoader")
	void adapterAutomaticallyServiceLoaded() {
		var jsonb = Jsonb.instance();

		var reader = jsonb.reader(Strings.EMPTY);
		var writer = jsonb.writer(OutputStream.nullOutputStream());

		assertThat(reader).isInstanceOf(MsgpackReader.class);
		assertThat(writer).isInstanceOf(MsgpackWriter.class);
	}
}