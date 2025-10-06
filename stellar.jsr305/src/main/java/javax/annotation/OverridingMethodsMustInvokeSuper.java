package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/// When this annotation is applied to a method, it indicates that if this method
/// is overridden in a subclass, the overriding method should invoke this method
/// (through method invocation on super).
///
/// An example of such method is [Object#finalize()].
///
/// @since 1.0
/// @author Brian Goetz
/// @author Mechite
@Documented
@Target(METHOD)
public @interface OverridingMethodsMustInvokeSuper {}