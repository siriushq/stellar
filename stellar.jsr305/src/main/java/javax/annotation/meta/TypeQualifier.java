package javax.annotation.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/// This qualifier is applied to an annotation to denote that the annotation
/// should be treated as a type qualifier.
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
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