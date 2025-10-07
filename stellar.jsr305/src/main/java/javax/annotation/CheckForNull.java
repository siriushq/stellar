package javax.annotation;

import java.lang.annotation.Documented;

import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;

/// The annotated element might be null, and uses of the element should check for null.
/// When this annotation is applied to a method, it applies to the method return value.
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented
@TypeQualifierNickname
@Nonnull(when = When.MAYBE)
public @interface CheckForNull {}