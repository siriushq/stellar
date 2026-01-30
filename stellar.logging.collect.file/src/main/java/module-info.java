import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.collect.file {
	requires org.jspecify;

	requires static sirius.stellar.logging;

	exports sirius.stellar.logging.collect.file;

	uses sirius.stellar.logging.collect.file.FileTechnique;

	provides sirius.stellar.logging.spi.LoggerExtension
		with sirius.stellar.logging.collect.file.FileCollector;
}