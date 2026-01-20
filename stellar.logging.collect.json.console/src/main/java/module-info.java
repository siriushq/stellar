import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.collect.json.console {
	requires org.jspecify;

	requires sirius.stellar.logging;
	requires sirius.stellar.logging.collect.json;
	requires sirius.stellar.logging.collect.console;

	exports sirius.stellar.logging.collect.json.console;

	provides sirius.stellar.logging.collect.console.ConsoleTechnique
		with sirius.stellar.logging.collect.json.console.JsonConsoleTechnique;
}