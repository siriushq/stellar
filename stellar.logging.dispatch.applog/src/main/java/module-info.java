import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.logging.applog {
	requires org.jspecify;

	requires sirius.stellar.logging;
	requires sirius.stellar.logging.jsr379x;
	requires io.avaje.applog;

	exports sirius.stellar.logging.dispatch.applog;

	provides io.avaje.applog.AppLog.Provider
		with sirius.stellar.logging.dispatch.applog.AppLogProvider;
}