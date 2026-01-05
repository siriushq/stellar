// SPDX-License-Identifier: BSD-3-Clause AND CC-BY-2.5
package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;

import javax.annotation.meta.TypeQualifierDefault;

/// This annotation can be applied to a package, class or method to indicate that the method
/// parameters in that element are nullable by default unless there is:
///
/// - An explicit nullness annotation
/// - The method overrides a method in a superclass
///   (in which case the annotation of the corresponding parameter in the superclass applies)
/// - There is a default parameter annotation applied to a more tightly nested element.
///
/// This annotation implies the same "nullness" as no annotation. However, it is different from
/// having no annotation, as it is inherited and can override [ParametersAreNonnullByDefault]
/// at an outer scope.
///
/// @see Nullable
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented
@Nullable
@TypeQualifierDefault(ElementType.PARAMETER)
public @interface ParametersAreNullableByDefault {}