package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.annotation.meta.TypeQualifier;
import javax.annotation.meta.When;

/// This annotation is used to denote String values that are untainted, i.e., properly
/// validated.
///
/// For example, this annotation should be used on the String value which represents an
/// SQL query to be passed to a given database.
///
/// When this annotation is applied to a method, it applies to the method return value.
///
/// @see Tainted
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented
@TypeQualifier
public @interface Untainted {
    When when() default When.ALWAYS;
}