package sirius.stellar.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.CLASS;

/// Marks the public API provided by the marked element as having low maturity,
/// and may change without notice. This does not refer to the quality of the
/// implementation behind said API.
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
public @interface Experimental {}