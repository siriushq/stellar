package javax.annotation;

import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.TypeQualifierValidator;
import javax.annotation.meta.When;
import java.lang.annotation.Documented;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/// This qualifier is used to denote String values that should be a regular expression.
/// When this annotation is applied to a method, it applies to the method return value.
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented
@Syntax("RegEx")
@TypeQualifierNickname
public @interface RegEx {

    When when() default When.ALWAYS;

    static class Checker implements TypeQualifierValidator<RegEx> {

		@Nonnull
        public When forConstantValue(@Nonnull RegEx annotation, Object value) {
			try {
				if (!(value instanceof String)) return When.NEVER;
				Pattern.compile((String) value);
				return When.ALWAYS;
			} catch (PatternSyntaxException exception) {
				return When.NEVER;
			}
		}
    }
}