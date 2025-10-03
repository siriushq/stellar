import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.jsr379x {

	requires org.jspecify;
	requires sirius.stellar.facility;
	requires sirius.stellar.logging;

	exports sirius.stellar.logging.dispatch.jsr379x;

	provides System.LoggerFinder with sirius.stellar.logging.dispatch.jsr379x.Jsr379Finder;
}