// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/// Specifies some aspects of a given method behavior, depending on arguments
/// provided in any given usage context.
///
/// This can be used by tools for advanced data flow analysis.
///
/// Identical to `org.jetbrains.annotations`/`org.springframework.lang`'s
/// `@Contract`, but does not have the `pure` attribute.
///
/// It only describes how the code works and doesn't add any functionality, by
/// means of code generation, enhancement, etc.
///
/// ### Syntax
/// ```
/// contract ::= (clause ';')* clause
/// clause ::= args '->' effect
/// args ::= ((arg ',')* arg )?
/// arg ::= value-constraint
/// value-constraint ::= '_' | 'null' | '!null' | 'false' | 'true'
/// effect ::= value-constraint | 'fail' | 'this' | 'new' | 'param<N>'
/// ```
///
/// ### Constraints
/// - `_`     &mdash; any value
/// - `null`  &mdash; any value
/// - `!null` &mdash; a value statically proved to be not-null
/// - `true`  &mdash; `true` boolean value
/// - `false` &mdash; `false` boolean value
///
/// ### Return values
/// - `fail`   &mdash; the method throws an exception, if the arguments satisfy
///                   argument constraints
/// - `new`    &mdash; the method returns a non-null new object which is
///                    distinct from any other object existing in the heap prior
///                    to method execution.
///
///                    If the method has no visible side effects, then we can be
///                    sure that the new object is not stored to any field/array
///                    and will be lost if the return value is not used.
/// - `this`   &mdash; the method returns its qualifier value (not applicable
///                    for static methods)
/// - `paramN` &mdash; the method returns its `N`th parameter value
///
/// # Usage examples
/// - `@Contract("_, null -> null")`
///    &mdash; returns null if the second argument is null
/// - `@Contract("true -> fail")`
///    &mdash; throws an exception when `true` is passed
/// - `@Contract("null -> fail; _ -> param1")`
///    &mdash; throws if the argument is null; otherwise returns it
///
/// @see <a href="http://tiny.cc/NullAway_custom_Contract">
///      use with NullAway via `-XepOpt:NullAway:CustomContractAnnotations=`</a>
/// @see <a href="https://grep.app/search?q=org.jetbrains.annotations.Contract">
///      original JetBrains @Contract annotation</a>
/// @see <a href="https://grep.app/search?q=org.springframework.lang.Contract">
///      original Spring Framework @Contract annotation</a>
///
/// @author Mahied Maruf (mechite)
/// @author Sebastien Deleuze (sdeleuze)
/// @since 1.0
@Documented
@Retention(CLASS)
@Target({
		METHOD,
		CONSTRUCTOR
})
public @interface Contract {

	/// The contract clauses describing causal relations between call
	/// arguments and the returned value.
	String value() default "";
}