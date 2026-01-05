// SPDX-License-Identifier: BSD-3-Clause AND CC-BY-2.5
package javax.annotation.concurrent;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/// The class to which this annotation is applied is not thread-safe. This
/// annotation primarily exists for clarifying the non-thread-safety of a class
/// that might otherwise be assumed to be thread-safe, despite the fact that it
/// is a bad idea to assume a class is thread-safe without good reason.
///
/// @see ThreadSafe
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented
@Target(ElementType.TYPE)
public @interface NotThreadSafe {}