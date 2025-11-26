package sirius.stellar.serialization.msgpack.jsonb;

import io.avaje.jsonb.Jsonb;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sirius.stellar.facility.Strings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.*;
import static sirius.stellar.serialization.msgpack.MessagePack.*;

final class MsgpackAdapterTest {

	@Test @DisplayName("adapter located automagically by Jsonb")
	void serviceLoaded() {
		var jsonb = Jsonb.instance();

		var reader = jsonb.reader(Strings.EMPTY);
		var writer = jsonb.writer(OutputStream.nullOutputStream());

		assertThat(reader).isInstanceOf(MsgpackReader.class);
		assertThat(writer).isInstanceOf(MsgpackWriter.class);
	}

	@Test @DisplayName("adapter correctly reads basic body")
	void reader() throws IOException {
		var packer = newDefaultBufferPacker();
		packer.packMapHeader(7)
			.packString("id").packInt(420)
			.packString("name").packString("John Doe")
			.packString("active").packBoolean(true)
			.packString("balance").packDouble(103.75)
			.packString("roles").packArrayHeader(2)
				.packString("admin")
				.packString("editor")
			.packString("profile").packMapHeader(2)
				.packString("age").packInt(30)
				.packString("country").packString("US")
			.packString("tags").packArrayHeader(2)
				.packMapHeader(2)
					.packString("key").packString("dept")
					.packString("value").packString("engineering")
				.packMapHeader(2)
					.packString("key").packString("level")
					.packString("value").packString("senior");

		var body = packer.toByteArray();
		packer.close();

		var reader = Jsonb.instance().reader(body);
		reader.beginObject();

		while (reader.hasNextField()) {
			switch (reader.nextField()) {
				case "id" -> assertThat(reader.readInt()).isEqualTo(420);
				case "name" -> assertThat(reader.readString()).isEqualTo("John Doe");
				case "active" -> assertThat(reader.readBoolean()).isTrue();
				case "balance" -> assertThat(reader.readDouble()).isEqualTo(103.75);
				case "roles" -> {
					reader.beginArray();
					assertThat(reader.readString()).isEqualTo("admin");
					assertThat(reader.readString()).isEqualTo("editor");
					reader.endArray();
				}
				case "profile" -> {
					reader.beginObject();
					while (reader.hasNextField()) {
						switch (reader.nextField()) {
							case "age" -> assertThat(reader.readInt()).isEqualTo(30);
							case "country" -> assertThat(reader.readString()).isEqualTo("US");
							default -> reader.skipValue();
						}
					}
					reader.endObject();
				}
				case "tags" -> {
					reader.beginArray();

					reader.beginObject();
					assertThat(reader.nextField()).isEqualTo("key");
					assertThat(reader.readString()).isEqualTo("dept");
					assertThat(reader.nextField()).isEqualTo("value");
					assertThat(reader.readString()).isEqualTo("engineering");
					reader.endObject();

					reader.beginObject();
					assertThat(reader.nextField()).isEqualTo("key");
					assertThat(reader.readString()).isEqualTo("level");
					assertThat(reader.nextField()).isEqualTo("value");
					assertThat(reader.readString()).isEqualTo("senior");
					reader.endObject();

					reader.endArray();
				}
				default -> reader.skipValue();
			}
		}

		reader.endObject();
	}

	@Test @DisplayName("adapter correctly writes basic body")
	void writer() throws IOException {
		var stream = new ByteArrayOutputStream();
		var writer = Jsonb.instance().writer(stream);

		writer.beginObject();
		writer.name("id");
		writer.value(420);

		writer.name("username");
		writer.value("John Doe");

		writer.name("active");
		writer.value(true);

		writer.name("balance");
		writer.value(103.75);

		writer.name("roles");
		writer.beginArray();
		writer.value("admin");
		writer.value("editor");
		writer.endArray();

		writer.name("profile");
		writer.beginObject();
		writer.name("age");
		writer.value(30);
		writer.name("country");
		writer.value("US");
		writer.endObject();

		writer.name("tags");
		writer.beginArray();

		writer.beginObject();
		writer.name("key");
		writer.value("dept");
		writer.name("value");
		writer.value("engineering");
		writer.endObject();

		writer.beginObject();
		writer.name("key");
		writer.value("level");
		writer.name("value");
		writer.value("senior");
		writer.endObject();

		writer.endArray();

		writer.endObject();
		writer.close();

		var unpacker = newDefaultUnpacker(stream.toByteArray());
		assertThat(unpacker.unpackMapHeader()).isEqualTo(7);

		assertThat(unpacker.unpackString()).isEqualTo("id");
		assertThat(unpacker.unpackInt()).isEqualTo(420);

		assertThat(unpacker.unpackString()).isEqualTo("username");
		assertThat(unpacker.unpackString()).isEqualTo("John Doe");

		assertThat(unpacker.unpackString()).isEqualTo("active");
		assertThat(unpacker.unpackBoolean()).isTrue();

		assertThat(unpacker.unpackString()).isEqualTo("balance");
		assertThat(unpacker.unpackDouble()).isEqualTo(103.75);

		assertThat(unpacker.unpackString()).isEqualTo("roles");
		assertThat(unpacker.unpackArrayHeader()).isEqualTo(2);
		assertThat(unpacker.unpackString()).isEqualTo("admin");
		assertThat(unpacker.unpackString()).isEqualTo("editor");

		assertThat(unpacker.unpackString()).isEqualTo("profile");
		assertThat(unpacker.unpackMapHeader()).isEqualTo(2);
		assertThat(unpacker.unpackString()).isEqualTo("age");
		assertThat(unpacker.unpackInt()).isEqualTo(30);
		assertThat(unpacker.unpackString()).isEqualTo("country");
		assertThat(unpacker.unpackString()).isEqualTo("US");

		assertThat(unpacker.unpackString()).isEqualTo("tags");
		assertThat(unpacker.unpackArrayHeader()).isEqualTo(2);

		assertThat(unpacker.unpackMapHeader()).isEqualTo(2);
		assertThat(unpacker.unpackString()).isEqualTo("key");
		assertThat(unpacker.unpackString()).isEqualTo("dept");
		assertThat(unpacker.unpackString()).isEqualTo("value");
		assertThat(unpacker.unpackString()).isEqualTo("engineering");

		assertThat(unpacker.unpackMapHeader()).isEqualTo(2);
		assertThat(unpacker.unpackString()).isEqualTo("key");
		assertThat(unpacker.unpackString()).isEqualTo("level");
		assertThat(unpacker.unpackString()).isEqualTo("value");
		assertThat(unpacker.unpackString()).isEqualTo("senior");
	}
}