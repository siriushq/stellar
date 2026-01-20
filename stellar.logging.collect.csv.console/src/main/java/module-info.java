import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.collect.csv.console {
	requires org.jspecify;

	requires sirius.stellar.logging;
	requires sirius.stellar.logging.collect.csv;
	requires sirius.stellar.logging.collect.console;

	exports sirius.stellar.logging.collect.csv.console;

	provides sirius.stellar.logging.collect.console.ConsoleTechnique
		with sirius.stellar.logging.collect.csv.console.CsvConsoleTechnique;
}