package sirius.stellar.logging.collect.csv.console;

import sirius.stellar.logging.LoggerMessage;
import sirius.stellar.logging.collect.console.ConsoleTechnique;
import sirius.stellar.logging.collect.csv.CsvAbstractTechnique;

import java.io.PrintStream;

/// Implementation of [ConsoleTechnique] for CSV (Comma-Separated Values) logging.
public final class CsvConsoleTechnique
		extends CsvAbstractTechnique
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