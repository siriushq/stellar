package sirius.stellar.facility.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/// Marks the provided element as having a low maturity and may change without any
/// notice, or possibly produce unwanted/unexpected behavior.
///
/// This is similar to [Internal] but anything annotated with it is intended to be
/// part of a public API. However, this can be combined with `@Internal` (but always
/// ordering `@Internal` first to ensure that it is clear) to mark an implementation
/// as immature. This simply serves as a note for future development to take place
/// on that particular element - perhaps it could be a performance-related enhancement
/// that could be done, etc.
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