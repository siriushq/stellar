package javax.annotation.concurrent;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// The class to which this annotation is applied is immutable. This means that
/// its state cannot be seen to change by callers. Of necessity this means that
/// all public fields are final, and that all public final reference fields refer
/// to other immutable objects, and that methods do not publish references to any
/// internal state which is mutable by implementation even if not by design.
/// Immutable objects may still have internal mutable state for purposes of
/// performance optimization; some state variables may be lazily computed, so
/// long as they are computed from immutable state and that callers cannot tell
/// the difference.
///
/// Immutable objects are inherently thread-safe; they may be passed between
/// threads or published without synchronization.
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented
@Target(ElementType.TYPE)
public @interface Immutable {}