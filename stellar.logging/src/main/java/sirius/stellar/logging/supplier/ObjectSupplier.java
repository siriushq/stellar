package sirius.stellar.logging.supplier;

import java.util.function.Supplier;

/**
 * A supplier that returns any kind of object.
 * <p>
 * This is used to ensure that the generic type erasures for logging
 * methods do not clash with each other.
 *
 * @see ThrowableSupplier
 *
 * @author Mahied Maruf (mechite)
 * @since 1.0
 */
public interface ObjectSupplier extends Supplier<Object> {

    /**
     * Returns the object.
     */
	@Override
    Object get();
}