// SPDX-License-Identifier: BSD-3-Clause AND CC-BY-2.5
package javax.annotation;

import javax.annotation.meta.When;
import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/// This annotation is used to denote a method whose return value should always
/// be checked after invoking the method.
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented
@Target({
		METHOD,
		CONSTRUCTOR,
		TYPE,
		PACKAGE
})
public @interface CheckReturnValue {
	When when() default When.ALWAYS;
}