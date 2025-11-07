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
/// type, use [Service.Provider] to mark providers of this service type, and use
/// [Service.Classifier] to declaratively create classifier artifacts for service
/// providers of optional dependencies.
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

//	/// Generates separate Maven classifier artifacts declaratively for service providers
//	/// of optional dependencies, directly integrating with Maven lifecycle.
//	///
//	/// You must author a module file for the separate classifier artifact using a file,
//	/// in the following case named `module-info$foo-provider.java`, with these rules:
//	///
//	/// - classifier module must export unique packages, not exported in the main module
//	/// - all packages exported for the classifier will be built for that artifact
//	/// - all packages exported for the classifier will be excluded from the main module
//	/// - the classifier module must have a unique name to the main module
//	/// - the classifier must depend on the main module
//	@Documented
//	@Target(MODULE) @Retention(CLASS)
//	@interface Classifier {
//
//		/// The name for the generated classifier and `module-info$(name).java`
//		/// source file. This is required.
//		String value();
//
//		/// The SPI to provide. This can be excluded to use inference.
//		Class<?> provides() default Void.class;
//
//		/// The implementation to provide with.
//		///
//		/// This should never be marked with [Service.Provider], etc., as this would mark
//		/// it as being a provider for the main artifact, rather than the classifier.
//		Class<?> with();
//	}
}