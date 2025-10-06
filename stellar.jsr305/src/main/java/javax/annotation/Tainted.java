package javax.annotation;

import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;
import java.lang.annotation.Documented;

/// This annotation is used to denote String values that are tainted, i.e., may come
/// from untrusted sources without proper validation.
///
/// For example, this annotation should be used on the String value which represents
/// raw input received from the web form.
///
/// When this annotation is applied to a method, it applies to the method return value.
///
/// @see Untainted
/// @since 1.0
/// @author Brian Goetz
/// @author Mechite
@Documented
@TypeQualifierNickname
@Untainted(when = When.MAYBE)
public @interface Tainted {}