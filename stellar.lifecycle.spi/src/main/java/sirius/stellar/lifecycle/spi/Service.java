package sirius.stellar.lifecycle.spi;

import org.jspecify.annotations.NonNull;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ServiceLoader;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/// Marks a class or interface as a service type.
///
/// Use this directly to force inference when creating providers of this service
/// type and use [Service.Provider] to mark providers of a given service type,
/// using inference if their subclasses lack [Service].
///
/// ```
/// @Service // mark to make A the chosen SPI
/// sealed class A permits B ...
///
/// non-sealed class B extends A ...
///
/// @Service.Provider
/// class C extends B ...
/// ```
@Documented
@Target(TYPE) @Retention(CLASS)
public @interface Service {

	/// Mark service providers as described in [ServiceLoader].
	///
	/// The annotation processor generates the configuration files that allow the
	/// annotated class to be loaded with [#load(Class)].
	///
	/// The annotated type must conform to the SPI specification, i.e. it must:
	/// - be a non-anonymous, non-inner, concrete class
	/// - have a no-arg constructor that is accessible publicly
	@Documented
	@Target(TYPE) @Retention(CLASS)
	@interface Provider {

		/// The SPIs to generate for (provide multiple if you implement multiple SPIs).
		/// If none are defined, it will be inferred.
		@NonNull Class<?>[] value() default {};
	}
}