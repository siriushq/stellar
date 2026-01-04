package sirius.stellar.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.CLASS;

/// Marks the provided element as being for internal use only and may change
/// without any notice, or possibly produce unwanted/unexpected behavior.
///
/// This should be used around a codebase on individual elements to mark
/// them as elements scoped for use internally only, as there is a limit to
/// how much encapsulation can be achieved elegantly.
///
/// When something can be encapsulated, e.g. with the use of access modifiers,
/// it should never be annotated (to avoid boilerplate).
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented @Inherited
@Retention(CLASS)
@Target({
		TYPE,
		FIELD,
		METHOD,
		CONSTRUCTOR,
		ANNOTATION_TYPE,
		PACKAGE,
		TYPE_USE,
		MODULE
})
public @interface Internal {}