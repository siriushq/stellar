package javax.annotation.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/// This annotation is applied to an annotation, and marks the annotation as being
/// a qualifier nickname. Applying a nickname annotation X to an element Y should
/// be interpreted as having the same meaning as applying all annotations of X
/// (other than QualifierNickname) to Y.
///
/// Thus, you might define a qualifier SocialSecurityNumber as follows:
///
/// ```
/// &#064;Documented
/// &#064;TypeQualifierNickname &#064;Pattern("[0-9]{3}-[0-9]{2}-[0-9]{4}")
/// &#064;interface SocialSecurityNumber {}
/// ```
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented
@Target(ElementType.ANNOTATION_TYPE)
public @interface TypeQualifierNickname {}