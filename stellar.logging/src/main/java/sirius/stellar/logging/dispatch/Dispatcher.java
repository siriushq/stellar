package sirius.stellar.logging.dispatch;

import sirius.stellar.logging.Logger;

import java.io.Serializable;

/// Represents a dispatcher.
///
/// This is used to wire any dispatchers that are not automatically service loaded by
/// a given logging facade. Dispatchers that are can simply not implement this interface.
///
/// This was created instead of simply referencing all the dispatchers directly in order to
/// allow for contributions to be made more easily to eventually cover every single logging
/// facade / implementation (before facades were used) so that not a single library isn't
/// able to push logging output to [Logger], though this violates the YAGNI rule.
///
/// It is a part of the public interface allowing people to respectively register their own
/// dispatchers given they are attempting to retrofit an unknown/proprietary logging system
/// that may be called across many internal packages.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public interface Dispatcher extends Serializable {

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