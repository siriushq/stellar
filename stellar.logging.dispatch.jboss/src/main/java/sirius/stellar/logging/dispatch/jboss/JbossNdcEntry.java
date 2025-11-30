package sirius.stellar.logging.dispatch.jboss;

/// Represents an entry in the NDC provided by [JbossProvider].
final class JbossNdcEntry {

	final String merged;
	final String current;

	JbossNdcEntry(String current) {
		this.merged = current;
		this.current = current;
	}

	JbossNdcEntry(JbossNdcEntry parent, String current) {
		this.merged = parent.merged + ' ' + current;
		this.current = current;
	}
}