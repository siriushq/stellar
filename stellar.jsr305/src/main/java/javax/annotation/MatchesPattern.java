package javax.annotation;

import javax.annotation.meta.TypeQualifier;
import javax.annotation.meta.TypeQualifierValidator;
import javax.annotation.meta.When;
import java.lang.annotation.Documented;
import java.util.regex.Pattern;

/**
 * This annotation is used to denote String values that should always match given pattern.
 * When this annotation is applied to a method, it applies to the method return value.
 *
 * @since 1.0
 * @author Brian Goetz
 * @author Mechite
 */
@Documented
@TypeQualifier(applicableTo = String.class)
public @interface MatchesPattern {

    @RegEx
    String value();

    int flags() default 0;

	class Checker implements TypeQualifierValidator<MatchesPattern> {

		@Nonnull
		public When forConstantValue(@Nonnull MatchesPattern annotation, Object value) {
			Pattern pattern = Pattern.compile(annotation.value(), annotation.flags());
			if (pattern.matcher(((String) value)).matches()) return When.ALWAYS;
			return When.NEVER;
		}
	}
}