package sirius.stellar.esthree;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static java.text.MessageFormat.format;

/// Abstraction of errors thrown by S3, providing statically accessible methods
/// for asserting if a given response is an error, and creating an instance
/// from a given [Document].
///
/// Member methods may return an empty string (i.e. `""`) if the parsed error
/// response is missing the field associated with that method (e.g. [#code()]).
///
/// @see <a href="https://tiny.cc/aws_s3_error_responses">AWS Reference</a>
public class EsthreeException extends IllegalStateException {

	private static final long serialVersionUID = -2991968104967447357L;
	private static final String EMPTY = "";

	private final String code;
	private final String message;
	private final String resource;

	private final String request;
	private final String host;

	protected EsthreeException(String code, String message, String resource, String request, String host) {
		super(format("{0}: {1}", code, message));

		this.code = code;
		this.message = message;
		this.resource = resource;

		this.request = request;
		this.host = host;
	}

	protected EsthreeException(Throwable throwable, String code, String message, String resource, String request, String host) {
		super(throwable);

		this.code = code;
		this.message = message;
		this.resource = resource;

		this.request = request;
		this.host = host;
	}

	/// Returns an error code uniquely identifying the error condition.
	public String code() {
		return this.code;
	}

	/// Returns a generic description of the error condition.
	public String message() {
		return this.message;
	}

	/// Returns the bucket or object involved in the error.
	public String resource() {
		return this.resource;
	}

	/// Returns the identifier for the request associated with the error.
	public String request() {
		return this.request;
	}

	/// Returns the extended identifier for the request associated with the error
	public String host() {
		return this.host;
	}

	@Override
	public String toString() {
		if (this.getCause() != null) return super.toString();
		return format("EsthreeException: {0}: {1}", this.code, this.message);
	}

	/// Returns whether the provided [Document] contains an S3 error response.
	static boolean detected(Document document) {
		return document.getElementsByTagName("Error").getLength() > 0;
	}

	/// Create an instance of [EsthreeException] reading the provided [Document].
	static EsthreeException of(Document document) {
		return new EsthreeException(
			element(document, "Code"),
			element(document, "Message"),
			element(document, "Resource"),
			element(document, "RequestId"),
			element(document, "HostId")
		);
	}

	/// Create an instance of [EsthreeException] reading the provided [Document],
	/// wrapping the provided cause exception.
	static <T extends Throwable> EsthreeException of(Document document, T exception) {
		return new EsthreeException(
			exception,
			element(document, "Code"),
			element(document, "Message"),
			element(document, "Resource"),
			element(document, "RequestId"),
			element(document, "HostId")
		);
	}

	/// Create an instance of [EsthreeException] wrapping the provided cause
	/// exception. Use if no error response document was returned.
	static <T extends Throwable> EsthreeException of(T exception) {
		return new EsthreeException(exception, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY);
	}

	/// Return the [String] text content of the element tag name in the provided [Document].
	private static String element(Document document, String name) {
		Node node = document.getElementsByTagName(name).item(0);
		return (node == null) ? EMPTY : node.getTextContent();
	}
}