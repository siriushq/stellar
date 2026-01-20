import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.collect.csv {
	requires org.jspecify;
	requires sirius.stellar.logging;
	requires sirius.stellar.facility;

	requires static sirius.stellar.logging.collect.console;
	requires static sirius.stellar.logging.collect.file;

	exports sirius.stellar.logging.collect.csv;

	provides sirius.stellar.logging.collect.file.FileTechnique
		with sirius.stellar.logging.collect.csv.CsvFileTechnique;

	provides sirius.stellar.logging.collect.console.ConsoleTechnique
		with sirius.stellar.logging.collect.csv.CsvConsoleTechnique;
}