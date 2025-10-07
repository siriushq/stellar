package javax.annotation.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/// This qualifier is applied to an annotation to denote that the annotation
/// defines a default type qualifier that is visible within the scope of the
/// element it is applied to.
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented
@Target(ElementType.ANNOTATION_TYPE)
public @interface TypeQualifierDefault {
    ElementType[] value() default {};
}