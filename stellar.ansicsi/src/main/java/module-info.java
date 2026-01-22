import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.ansicsi {
	requires org.jspecify;

	exports sirius.stellar.ansicsi;

	uses sirius.stellar.ansicsi.TerminalExtension;
}