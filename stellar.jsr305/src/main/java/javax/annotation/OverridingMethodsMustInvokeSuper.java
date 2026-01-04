// SPDX-License-Identifier: BSD-3-Clause AND CC-BY-2.5
package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/// When this annotation is applied to a method, it indicates that if this method
/// is overridden in a subclass, the overriding method should invoke this method
/// (through method invocation on super).
///
/// An example of such method is [Object#finalize()].
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented
@Target(METHOD)
public @interface OverridingMethodsMustInvokeSuper {}