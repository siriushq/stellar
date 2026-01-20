package sirius.stellar.logging.collect.csv;

import sirius.stellar.logging.LoggerMessage;

import java.util.Arrays;
import java.util.StringJoiner;

import static java.util.stream.Collectors.joining;

/// Base class for CSV (Comma-Separated Values) logging technique.
sealed abstract class CsvAbstractTechnique
	permits CsvFileTechnique, CsvConsoleTechnique {

	/// Return a CSV formatted version of the provided message.
	protected String format(LoggerMessage message) {
		StringJoiner joiner = new StringJoiner("\",\"", "\"", "\"\n");
		joiner.add(message.time().toString())
			  .add(message.level().toString())
			  .add(message.thread())
			  .add(message.name());
		String lines = Arrays.stream(message.text()
				.replaceAll("\"", "`")
				.replaceAll("'", "`")
				.split("\n"))
				.map(line -> "'" + line + "'")
				.collect(joining());
		joiner.add(lines);
		return joiner.toString();
	}
}