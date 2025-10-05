package sirius.stellar.facility.doctation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/// Marks the provided element as being for internal use only and may change
/// without any notice, or possibly produce unwanted/unexpected behavior.
///
/// This should be used around the codebase on individual elements to mark
/// them as elements scoped for use internally only, as there is a limit to
/// how much encapsulation can be achieved elegantly - but when it is very
/// clear that something is for internal use only, for example when it is
/// a `private (static) final` field, or `private` method, then
/// you should not annotate it as that would lead to excessive boilerplate.
///
/// However, with things such as private classes, they can still be supplied
/// as e.g. the return type of any method, especially if they implement any
/// publicly accessible interface. A good example of something like this is
/// the [sirius.stellar.facility.tuple] classes, as they have immutable
/// and mutable implementations marked with this annotation - the methods to
/// obtain those instances are guaranteed to be a stable API unlike the
/// implementary classes which are an implementation note that should never
/// be relied on.
///
/// Those implementary classes are package-private, though, not private, to
/// reflect upon the fact that as an implementation it is exposed as part of
/// the public API, but shouldn't be accessed through, e.g., reflective access.
///
/// Even given a private class though, this annotation is suitable. Anything
/// that could easily, undesirably, become part of the public API should be
/// annotated with this in order to prevent this from happening.
///
/// @since 1.0
/// @author Mechite
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
public @interface Internal {}