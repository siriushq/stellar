package javax.annotation.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// This qualifier is applied to an annotation to denote that the annotation
/// should be treated as a type qualifier.
///
/// @since 1.0
/// @author Brian Goetz
/// @author Mechite
@Documented
@Target(ElementType.ANNOTATION_TYPE)
public @interface TypeQualifier {

    /// Describes the kinds of values the qualifier can be applied to. If a
    /// numeric class is provided (e.g., Number.class or Integer.class) then the
    /// annotation can also be applied to the corresponding primitive numeric
    /// types.
    ///
    /// @return The type of the values the original annotation can be applied to.
    Class<?> applicableTo() default Object.class;
}