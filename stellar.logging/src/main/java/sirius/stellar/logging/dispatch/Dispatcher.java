package sirius.stellar.logging.dispatch;

/// Represents a dispatcher.
///
/// This is used to wire any dispatchers that are not automatically service loaded by
/// a given logging facade. Dispatchers that are can simply not implement this interface.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Deprecated
public interface Dispatcher {

	/// Run when a dispatcher is wired.
	///
	/// This method invocation is not expected to be reversible; i.e., any static
	/// field changes, file, database changes, etc., do not need to be reversible,
	/// and permanent changes can be made in this method to register dispatchers.
	/// However, it is expected that it is reversible with a full program restart.
	///
	/// @since 1.0
	void wire() throws Throwable;

	/// Provides an instance of a [Dispatcher].
	///
	/// This was created to allow for named dispatchers to be provided given that
	/// registering the dispatcher isn't done automatically by a facade, but named
	/// logging is supported with whatever technique is preferred for registration
	/// instead.
	///
	/// @author Mahied Maruf (mechite)
	/// @since 1.0
	interface Provider {
		Dispatcher create();
	}
}