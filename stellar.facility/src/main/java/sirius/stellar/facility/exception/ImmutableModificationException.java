package sirius.stellar.facility.exception;

import java.io.Serial;

/// Thrown to indicate that an operation (modification) was requested on an immutable object.
///
/// This extends [UnsupportedOperationException] instead of [ImmutableModificationException] as
/// in the cases that this exception is suitable, that exception is semantically a very direct
/// abstraction to this one and [UnsupportedOperationException] may be directly caught in edge
/// cases. This is only done for convenience.
///
/// @since 1.0
/// @author Mechite
public class ImmutableModificationException extends UnsupportedOperationException {

	@Serial
	private static final long serialVersionUID = 1120179661764979139L;

	/// Constructor for an immutable modification exception with no message.
	/// The default message is `"Cannot modify an immutable object"`.
	///
	/// @see ImmutableModificationException#ImmutableModificationException(String)
	/// @since 1.0
	public ImmutableModificationException() {
		super("Cannot modify an immutable object");
	}

	/// Constructor for an immutable modification exception with the specified detail message.
	///
	/// @see ImmutableModificationException#ImmutableModificationException()
	/// @since 1.0
	public ImmutableModificationException(String message) {
		super(message);
	}

	/// Constructs a new exception with the specified cause and the detail message of the provided [Throwable].
	///
	/// @param cause The provided cause, accessible with the [#getCause()] method.
	/// @see ImmutableModificationException#ImmutableModificationException(String, Throwable)
	/// @since 1.0
	public ImmutableModificationException(Throwable cause) {
		super(cause);
	}

	/// Constructs a new exception with the specified detail message and cause.
	///
	/// Note that the detail message associated with cause is not automatically
	/// incorporated in this exception's detail message.
	///
	/// @param cause The provided cause, accessible with the [#getCause()] method.
	/// @see ImmutableModificationException#ImmutableModificationException(Throwable)
	/// @since 1.0
	public ImmutableModificationException(String message, Throwable cause) {
		super(message, cause);
	}
}