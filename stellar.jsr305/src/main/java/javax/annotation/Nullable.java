package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;

/// The annotated element could be null under some circumstances.
///
/// In general, this means developers will have to read the documentation to
/// determine when a null value is acceptable and whether it is necessary to
/// check for a null value.
///
/// This annotation is useful mostly for overriding a [Nonnull] annotation.
/// Static analysis tools should generally treat the annotated items as though they
/// had no annotation, unless they are configured to minimize false negatives.
/// Use [CheckForNull] to indicate that the element value should always be checked
/// for a null value.
///
/// When this annotation is applied to a method, it applies to the method return value.
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented
@TypeQualifierNickname
@Nonnull(when = When.UNKNOWN)
public @interface Nullable {}