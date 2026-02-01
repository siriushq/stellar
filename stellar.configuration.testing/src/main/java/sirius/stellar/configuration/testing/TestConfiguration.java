package sirius.stellar.configuration.testing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import sirius.stellar.configuration.Configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/// Annotation for the testing of applications and libraries,
/// providing declarative mutation of [Configuration].
///
/// This can be applied to test classes or `@`[Test] methods.
///
/// @since 1.0
@Retention(RUNTIME)
@Target({ METHOD, TYPE })
@ExtendWith(TestConfigurationJunit.class)
public @interface TestConfiguration {

	/// Immutable key-value configuration.
	/// @since 1.0
	String[] value();
}