package javax.annotation;

import javax.annotation.meta.TypeQualifier;
import javax.annotation.meta.TypeQualifierValidator;
import javax.annotation.meta.When;
import java.lang.annotation.Documented;

/**
 * This annotation is used to annotate a value that should only contain nonnegative values.
 * When this annotation is applied to a method, it applies to the method return value.
 *
 * @since 1.0
 * @author Brian Goetz
 * @author Mechite
 */
@Documented
@TypeQualifier(applicableTo = Number.class)
public @interface Nonnegative {

    When when() default When.ALWAYS;

    class Checker implements TypeQualifierValidator<Nonnegative> {

		@Nonnull
        public When forConstantValue(@Nonnull Nonnegative annotation, Object value) {
            if (!(value instanceof Number)) return When.NEVER;

			boolean isNegative;
			Number number = (Number) value;

			if (number instanceof Long) isNegative = number.longValue() < 0;
			else if (number instanceof Double) isNegative = number.doubleValue() < 0;
			else if (number instanceof Float) isNegative = number.floatValue() < 0;
			else isNegative = number.intValue() < 0;

            return isNegative ? When.NEVER : When.ALWAYS;
        }
    }
}