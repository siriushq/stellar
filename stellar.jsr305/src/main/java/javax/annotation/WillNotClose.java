// SPDX-License-Identifier: BSD-3-Clause AND CC-BY-2.5
package javax.annotation;

import java.lang.annotation.Documented;

/// Used to annotate a method parameter to indicate that this method will not
/// close the resource.
///
/// @see WillClose
/// @see WillCloseWhenClosed
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented
public @interface WillNotClose {}