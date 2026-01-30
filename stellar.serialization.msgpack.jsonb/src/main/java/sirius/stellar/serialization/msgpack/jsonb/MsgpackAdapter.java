package sirius.stellar.serialization.msgpack.jsonb;

import io.avaje.json.JsonReader;
import io.avaje.json.JsonWriter;
import io.avaje.json.PropertyNames;
import io.avaje.json.stream.BufferedJsonWriter;
import io.avaje.json.stream.BytesJsonWriter;
import io.avaje.json.stream.JsonOutput;
import io.avaje.json.stream.JsonStream;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import static sirius.stellar.serialization.msgpack.MessagePack.newDefaultPacker;
import static sirius.stellar.serialization.msgpack.MessagePack.newDefaultUnpacker;

/// Implementation of [JsonStream] for MessagePack.
/// A usage exemplar for explicit usage is as follows:
/// ```
/// Jsonb jsonb = Jsonb.builder()
///     .adapter(new MsgpackAdapter())
///     .build();
/// ```
///
/// ### Service loading initialise
/// Including the `stellar-serialization-msgpack-jsonb` dependency in the class-path
/// or module-path will, by default, cause this adapter to be automatically selected via
/// service loading.
///
/// Should this be undesirable, e.g., if the default constructor options for the
/// serialization of null values, empty arrays, etc. is undesirable, explicitly using
/// the adapter as demonstrated above would allow this to be configured as desired.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public class MsgpackAdapter implements JsonStream {

	private final boolean serializeNulls;
	private final boolean serializeEmpty;
	private final boolean failOnUnknown;
	private final boolean failNonBinary;

	/// No-argument constructor that can create an adapter with the default
	/// settings as outlined below (not the same as the defaults when using
	/// [MsgpackAdapterFactory] and `avaje-jsonb` automatic configuration):
	///
	/// - serializeNulls true
	/// - serializeEmpty true
	/// - failOnUnknown false
	/// - failNonBinary true
	///
	/// @see MsgpackAdapter#MsgpackAdapter(boolean, boolean, boolean, boolean)
	/// @see MsgpackAdapterFactory
	/// @since 1.0
	public MsgpackAdapter() {
		this(true, true, false, false);
	}

	/// Constructor that creates an adapter with the provided settings.
	///
	/// @param serializeNulls Whether to enable the serialization of `null` values.
	/// @param serializeEmpty Whether to enable the serialization of empty _arrays_.
	/// @param failOnUnknown Whether to fail when deserializing unknown properties.
	/// @param failNonBinary Whether to fail [Reader]/[Writer]-based methods.
	///
	/// @see MsgpackAdapter#MsgpackAdapter()
	/// @since 1.0
	public MsgpackAdapter(boolean serializeNulls, boolean serializeEmpty, boolean failOnUnknown, boolean failNonBinary) {
		this.serializeNulls = serializeNulls;
		this.serializeEmpty = serializeEmpty;
		this.failOnUnknown = failOnUnknown;
		this.failNonBinary = failNonBinary;
	}

	@Override
	public JsonReader reader(String string) {
		return this.reader(string.getBytes());
	}

	@Override
	public JsonReader reader(byte[] bytes) {
		return new MsgpackReader(newDefaultUnpacker(bytes), this.failOnUnknown);
	}

	@Override
	public JsonReader reader(Reader reader) {
		if (this.failNonBinary) throw new UnsupportedOperationException();
		return this.reader(new ReaderInputStream(reader));
	}

	@Override
	public JsonReader reader(InputStream stream) {
		return new MsgpackReader(newDefaultUnpacker(stream), this.failOnUnknown);
	}

	@Override
	public JsonWriter writer(Writer writer) {
		if (this.failNonBinary) throw new UnsupportedOperationException();
		return this.writer(new WriterOutputStream(writer));
	}

	@Override
	public JsonWriter writer(JsonOutput output) {
		return this.writer(output.unwrapOutputStream());
	}

	@Override
	public JsonWriter writer(OutputStream stream) {
		return new MsgpackWriter(newDefaultPacker(stream), this.serializeNulls, this.serializeEmpty);
	}

	@Override
	public BufferedJsonWriter bufferedWriter() {
		return new MsgpackBufferedWriter(this.serializeNulls, this.serializeEmpty);
	}

	@Override
	public BytesJsonWriter bufferedWriterAsBytes() {
		return new MsgpackBytesWriter(this.serializeNulls, this.serializeEmpty);
	}

	@Override
	public PropertyNames properties(String... names) {
		return new MsgspackPropertyNames(names);
	}
}