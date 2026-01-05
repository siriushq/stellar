// SPDX-License-Identifier: BSD-3-Clause AND CC-BY-2.5
package javax.annotation.meta;

import java.lang.annotation.Documented;

/// This annotation can be applied to the value() element of an annotation that
/// is annotated as a TypeQualifier.
///
/// For example, the following defines a type qualifier such that if you know a
/// value is {@literal @Foo(1)}, then the value cannot be {@literal @Foo(2)} or {{@literal @Foo(3)}.
///
/// ```
/// &#064;TypeQualifier &#064;interface Foo {
///     &#064;Exclusive int value();
/// }
/// ```
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented
public @interface Exclusive {}