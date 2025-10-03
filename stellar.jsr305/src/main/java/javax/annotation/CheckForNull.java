package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;

/**
 * The annotated element might be null, and uses of the element should check for null.
 * When this annotation is applied to a method, it applies to the method return value.
 *
 * @since 1.0
 * @author Brian Goetz
 * @author Mechite
 */
@Documented
@TypeQualifierNickname
@Nonnull(when = When.MAYBE)
public @interface CheckForNull {}