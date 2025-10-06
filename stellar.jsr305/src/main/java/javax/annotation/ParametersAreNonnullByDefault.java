package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.annotation.meta.TypeQualifierDefault;

/// This annotation can be applied to a package, class or method to indicate that the method
/// parameters in that element are nonnull by default unless there is:
///
/// - An explicit nullness annotation
/// - The method overrides a method in a superclass
///   (in which case the annotation of the corresponding parameter in the superclass applies)
/// - There is a default parameter annotation (like [ParametersAreNullableByDefault])
///   applied to a more tightly nested element.
///
/// @see Nonnull
/// @since 1.0
/// @author Brian Goetz
/// @author Mechite
@Documented
@Nonnull
@TypeQualifierDefault(ElementType.PARAMETER)
public @interface ParametersAreNonnullByDefault {}