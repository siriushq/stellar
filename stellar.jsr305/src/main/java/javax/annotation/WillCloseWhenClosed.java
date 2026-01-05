// SPDX-License-Identifier: BSD-3-Clause AND CC-BY-2.5
package javax.annotation;

import java.lang.annotation.Documented;

/// Used to annotate a constructor/factory parameter to indicate that returned
/// object (X) will close the resource when X is closed.
///
/// @see WillClose
/// @see WillNotClose
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented
public @interface WillCloseWhenClosed {}