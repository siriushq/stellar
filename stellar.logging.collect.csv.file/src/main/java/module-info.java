import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.collect.csv.file {
	requires org.jspecify;

	requires sirius.stellar.logging;
	requires sirius.stellar.logging.collect.csv;
	requires sirius.stellar.logging.collect.file;

	exports sirius.stellar.logging.collect.csv.file;

	provides sirius.stellar.logging.collect.file.FileTechnique
		with sirius.stellar.logging.collect.csv.file.CsvFileTechnique;
}