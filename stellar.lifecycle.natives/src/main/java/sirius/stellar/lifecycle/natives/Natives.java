package sirius.stellar.lifecycle.natives;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/// Marks a package as requiring the lookup of native libraries that are listed
/// in the [#value()] element of this annotation.
///
/// To use this, supply descriptive names for each shared library that you wish
/// to reference, and run the processor (build project) to create lookup stubs.
///
/// Then, use an accompanying build plugin to generate the artifacts for each
/// shared library that you wish to reference.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
@Documented
@Retention(CLASS)
@Target(PACKAGE)
public @interface Natives {

	/// The native library artifact names, for the lookup stubs to generate.
	/// These must be defined in `camelCase` to create pragmatic names.
	/// @since 1.0
	String[] value();

	/// Applies the provided header to the beginning of any generated stubs.
	///
	/// This is useful for adding a custom `@Generated` annotation usage, etc.
	/// Use a multi-line string, i.e. `"""`, and FQNs (fully qualified names).
	///
	/// @since 1.0
	String header() default "";
}