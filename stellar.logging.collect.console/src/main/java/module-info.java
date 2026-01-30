import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.collect.console {
	requires org.jspecify;

	requires sirius.stellar.logging;
	requires sirius.stellar.ansicsi;

	exports sirius.stellar.logging.collect.console;

	uses sirius.stellar.logging.collect.console.ConsoleTechnique;

	provides sirius.stellar.logging.spi.LoggerExtension
		with sirius.stellar.logging.collect.console.ConsoleCollector;
}