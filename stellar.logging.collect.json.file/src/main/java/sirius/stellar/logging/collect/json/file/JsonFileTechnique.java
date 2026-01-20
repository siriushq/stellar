package sirius.stellar.logging.collect.json.file;

import sirius.stellar.logging.LoggerMessage;
import sirius.stellar.logging.collect.file.FileTechnique;
import sirius.stellar.logging.collect.json.JsonAbstractTechnique;

/// Implementation of [FileTechnique] for JSON (JavaScript Object Notation) logging.
public final class JsonFileTechnique
		extends JsonAbstractTechnique
		implements FileTechnique {

	@Override
	public String format(LoggerMessage message) {
		return super.format(message);
	}

	@Override
	public String extension() {
		return ".json";
	}
}