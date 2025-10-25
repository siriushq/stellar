// SPDX-License-Identifier: BSD-3-Clause AND CC-BY-2.5
package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;

/// Used to annotate a value of unknown sign.
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented
@TypeQualifierNickname
@Nonnegative(when = When.UNKNOWN)
public @interface Signed {}