import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.ansicsi.windows {
	requires org.jspecify;
	requires sirius.stellar.ansicsi;

	requires static org.jnrproject.ffi;

	provides sirius.stellar.ansicsi.TerminalExtension
		with sirius.stellar.ansicsi.windows.WindowsTerminalExtension;
}