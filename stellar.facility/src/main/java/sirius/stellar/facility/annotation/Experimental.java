package sirius.stellar.facility.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/// Marks the provided element as having a low maturity and may change without any
/// notice, or possibly produce unwanted/unexpected behavior.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented @Inherited
@Target({
		TYPE,
		FIELD,
		METHOD,
		CONSTRUCTOR,
		ANNOTATION_TYPE,
		PACKAGE,
		TYPE_USE,
		MODULE,
		RECORD_COMPONENT
})
public @interface Experimental {}