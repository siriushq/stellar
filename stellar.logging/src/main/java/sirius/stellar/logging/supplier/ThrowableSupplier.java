package sirius.stellar.logging.supplier;

import java.util.function.Supplier;

/**
 * A supplier that returns any kind of throwable.
 * <p>
 * This is used to ensure that the generic type erasures for logging
 * methods do not clash with each other.
 *
 * @see ObjectSupplier
 * @since 1.0
 * @author Mechite
 */
public interface ThrowableSupplier extends Supplier<Throwable> {

    /**
     * Returns the throwable.
     */
	@Override
    Throwable get();
}