package sirius.stellar.logging.collect.json;

import sirius.stellar.logging.LoggerMessage;

import static java.lang.ThreadLocal.withInitial;

/// Base class for JSON (JavaScript Object Notation) logging technique.
sealed abstract class JsonAbstractTechnique
		permits JsonFileTechnique, JsonConsoleTechnique {

	private final ThreadLocal<StringBuilder> builder;
	private final char[] hex;

	JsonAbstractTechnique() {
		this.builder = withInitial(() -> new StringBuilder(128));
		this.hex = "0123456789abcdef".toCharArray();
	}

	/// Return a JSON formatted version of the provided message.
	protected String format(LoggerMessage message) {
		StringBuilder builder = this.builder.get();
		builder.setLength(0);
		builder.append('{');

		builder.append("\"time\":");
		this.json(builder, message.time().toString());

		builder.append(",\"level\":");
		this.json(builder, message.level().display());

		builder.append(",\"thread\":");
		this.json(builder, message.thread());

		builder.append(",\"logger\":");
		this.json(builder, message.name());

		builder.append(",\"message\":");
		this.json(builder, message.text());

		builder.append("}\n");
		return builder.toString();
	}

	/// Write the provided [Object] as JSON to the provided [StringBuilder].
	private <T> void json(StringBuilder builder, T value) {
		String string = String.valueOf(value);
		builder.append('"');

		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (c >= 0x20 && c != '"' && c != '\\') {
				builder.append(c);
				continue;
			}
			switch (c) {
			case '"':
				builder.append("\\\"");
				break;
			case '\\':
				builder.append("\\\\");
				break;
			case '\n':
				builder.append("\\n");
				break;
			case '\r':
				builder.append("\\r");
				break;
			case '\t':
				builder.append("\\t");
				break;
			case '\b':
				builder.append("\\b");
				break;
			case '\f':
				builder.append("\\f");
				break;
			default:
				builder.append("\\u00");
				builder.append(this.hex[(c >>> 4) & 0xF]);
				builder.append(this.hex[c & 0xF]);
				break;
			}
		}

		builder.append('"');
	}
}