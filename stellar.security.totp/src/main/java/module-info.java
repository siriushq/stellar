import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.security.totp {
	requires org.jspecify;
	requires sirius.stellar.serialization.base32x;

	exports sirius.stellar.security.totp;

	uses sirius.stellar.security.totp.Totp.Builder;
}