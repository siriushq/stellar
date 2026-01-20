package sirius.stellar.logging.collect.json.console;

import sirius.stellar.logging.LoggerMessage;
import sirius.stellar.logging.collect.console.ConsoleTechnique;
import sirius.stellar.logging.collect.json.JsonAbstractTechnique;

import java.io.PrintStream;

/// Implementation of [ConsoleTechnique] for JSON (JavaScript Object Notation) logging.
public final class JsonConsoleTechnique
		extends JsonAbstractTechnique
		implements ConsoleTechnique {

	@Override
	public String format(LoggerMessage message) {
		return super.format(message);
	}

	@Override
	public PrintStream destination() {
		return System.out;
	}
}