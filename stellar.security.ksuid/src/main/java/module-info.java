import org.jspecify.annotations.NullMarked;

@NullMarked
module sirius.stellar.security.ksuid {
	requires org.jspecify;
	requires sirius.stellar.serialization.base62x;

	exports sirius.stellar.security.ksuid;

	uses sirius.stellar.security.ksuid.Ksuid.Builder;
}