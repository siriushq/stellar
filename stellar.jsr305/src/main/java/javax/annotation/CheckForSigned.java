package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;

/// Used to annotate a value that may be either negative or nonnegative, and
/// indicates that uses of it should check for negative values before using it
/// in a way that requires the value to be nonnegative, and check for it being
/// nonnegative before using it in a way that requires it to be negative.
///
/// @since 1.0
/// @author Brian Goetz
/// @author Mechite
@Documented
@TypeQualifierNickname
@Nonnegative(when = When.MAYBE)
public @interface CheckForSigned {}