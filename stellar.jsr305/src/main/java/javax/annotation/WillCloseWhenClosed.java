package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/// Used to annotate a constructor/factory parameter to indicate that returned
/// object (X) will close the resource when X is closed.
///
/// @see WillClose
/// @see WillNotClose
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented
public @interface WillCloseWhenClosed {}