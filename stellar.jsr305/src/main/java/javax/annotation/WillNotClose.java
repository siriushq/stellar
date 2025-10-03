package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to annotate a method parameter to indicate that this method will not
 * close the resource.
 *
 * @see WillClose
 * @see WillCloseWhenClosed
 *
 * @since 1.0
 * @author Brian Goetz
 * @author Mechite
 */
@Documented
public @interface WillNotClose {}