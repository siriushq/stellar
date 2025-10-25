// SPDX-License-Identifier: BSD-3-Clause AND CC-BY-2.5
package javax.annotation;

import javax.annotation.meta.TypeQualifier;
import javax.annotation.meta.TypeQualifierValidator;
import javax.annotation.meta.When;
import java.lang.annotation.Documented;

/// The annotated element must not be null.
/// Annotated fields must not be null after construction has completed.
/// When this annotation is applied to a method, it applies to the method return value.
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented
@TypeQualifier
public @interface Nonnull {

	When when() default When.ALWAYS;

	class Checker implements TypeQualifierValidator<Nonnull> {

		@Nonnull
		public When forConstantValue(@Nonnull Nonnull qualifierArgument, Object value) {
			return (value == null) ? When.NEVER : When.ALWAYS;
		}
	}
}