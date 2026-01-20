package sirius.stellar.logging.collect.csv;

import sirius.stellar.logging.LoggerMessage;
import sirius.stellar.logging.collect.file.FileTechnique;

/// Implementation of [FileTechnique] for CSV (Comma-Separated Values) logging.
public final class CsvFileTechnique
		extends CsvAbstractTechnique
		implements FileTechnique {

	@Override
	public String format(LoggerMessage message) {
		return super.format(message);
	}

	@Override
	public String header() {
		return "\"time\",\"level\",\"thread\",\"name\",\"text\"\n";
	}

	@Override
	public String extension() {
		return ".csv";
	}
}