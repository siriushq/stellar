package javax.annotation.concurrent;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/// The class to which this annotation is applied is thread-safe. This means that
/// no sequences of accesses (reads and writes to public fields, calls to public
/// methods) may put the object into an invalid state, regardless of the
/// interleaving of those actions by the runtime, and without requiring any
/// additional synchronization or coordination on the part of the caller.
///
/// @see NotThreadSafe
///
/// @since 1.0
/// @author Brian Goetz
/// @author Mechite
@Documented
@Target(ElementType.TYPE)
public @interface ThreadSafe {}