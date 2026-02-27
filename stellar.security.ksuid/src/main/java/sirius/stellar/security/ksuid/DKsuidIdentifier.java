package sirius.stellar.security.ksuid;

/// Domain implementation of [Ksuid.Identifier].
final class DKsuidIdentifier implements Ksuid.Identifier {

	private final char[] value;

	DKsuidIdentifier(char[] value) {
		this.value = value;
	}

	@Override
	public String string() {
		return new String(this.value);
	}

	@Override
	public char[] value() {
		return this.value;
	}

	@Override
	public String toString() {
		return this.string();
	}
}