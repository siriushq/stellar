// SPDX-License-Identifier: BSD-3-Clause AND CC-BY-2.5
package javax.annotation.meta;

/// Used to describe the relationship between a qualifier T and the set of values
/// S possible on an annotated element.
///
/// In particular, an issues should be reported if an ALWAYS or MAYBE value is
/// used where a NEVER value is required, or if a NEVER or MAYBE value is used
/// where an ALWAYS value is required.
///
/// @author Brian Goetz (briangoetz)
/// @author Mahied Maruf (mechite)
/// @since 1.0
public enum When {

    /// S is a subset of T.
    ALWAYS,

    /// Nothing definitive is known about the relation between S and T.
    UNKNOWN,

    /// S intersection T is non-empty and S - T is non-empty.
    MAYBE,

    /// S intersection T is empty.
    NEVER
}