package javax.annotation.meta;

import java.lang.annotation.Annotation;

import javax.annotation.Nonnull;

/// Interface for validating whether a constant value is a member
/// of a set of values denoted by a type qualifier annotation.
///
/// @since 1.0
/// @author Brian Goetz
/// @author Mechite
public interface TypeQualifierValidator<A extends Annotation> {

    /// Given a type qualifier, check to see if a known specific constant value
    /// is an instance of the set of values denoted by the qualifier.
    ///
    /// @return Whether the value is a member of the values denoted by the
    /// provided type qualifier.
    @Nonnull
    When forConstantValue(@Nonnull A annotation, Object value);
}