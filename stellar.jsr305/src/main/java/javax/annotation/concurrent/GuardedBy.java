// SPDX-License-Identifier: BSD-3-Clause AND CC-BY-2.5
package javax.annotation.concurrent;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/// The field or method to which this annotation is applied can only be accessed when holding a particular lock, which
/// may be a built-in (synchronization) lock, or may be an explicit [java.util.concurrent.locks.Lock].
///
/// The argument determines which lock guards the annotated field or method:
///
/// - this : The string literal "this" means that this field is guarded by the class in which it is defined.
/// - class-name.this : For inner classes, it may be necessary to disambiguate 'this';
///   the class-name.this designation allows you to specify which 'this' reference is intended
/// - itself : For reference fields only; the object to which the field refers.
/// - field-name : The lock object is referenced by the (instance or static) field specified by field-name.
/// - class-name.field-name : The lock object is reference by the static field specified by class-name.field-name.
/// - method-name() : The lock object is returned by calling the named nil-ary method.
/// - class-name.class : The Class object for the specified class should be used as the lock object.
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Target({
		FIELD,
		METHOD
})
public @interface GuardedBy {
    String value();
}