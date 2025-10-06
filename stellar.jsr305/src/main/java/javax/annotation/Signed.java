package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;

/// Used to annotate a value of unknown sign.
///
/// @since 1.0
/// @author Brian Goetz
/// @author Mechite
@Documented
@TypeQualifierNickname
@Nonnegative(when = When.UNKNOWN)
public @interface Signed {}