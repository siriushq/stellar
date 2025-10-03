package sirius.stellar.facility.concurrent;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static sirius.stellar.facility.Strings.*;

/**
 * A wrapper around a given object of type {@code T}, with methods to wait until it is available.
 * <p>
 * This is thread-safe, and the {@link Latched#set(Object)} method can be invoked from any thread;
 * once {@link Latched#release()} has been invoked, {@link Latched#get()} will return on every
 * thread waiting for it, even if the object is {@code null}; the control on when the object is
 * available is separate to the actual assignment of the object.
 * <p>
 * {@link Latched#toString()} returns a message that contains the locked object, whether the latch
 * has been released or not - it can potentially display {@code "null"}, but is entirely null-safe
 * and uses {@link String#valueOf(Object)} in order to obtain this value. The message will appear
 * as {@code Latched[object=*]}, where {@code *} is {@code String.valueOf(latched.reference.get())}.
 * <p>
 * {@link Latched#hashCode()} returns a hash code for the latched object if it has been set, or
 * zero if it has not (as {@link Objects#hashCode(Object)} is called).
 * <p>
 * {@link Latched#equals(Object)} will return true if this latched object is equal to itself, false
 * if the object it is compared to null, or true if the object it is compared to is another latched
 * object where the set object is deeply equal to this latched object's set object. The deep equality
 * is calculated with {@link Objects#deepEquals(Object, Object)}. If the latched object is still
 * locked, it will instead return false rather than trying to await the object's release as that
 * qualifies the lack of equality. This is checked with {@link Latched#locked()}, which in turn
 * invokes {@link CountDownLatch#getCount()} which may not be desirable behaviour.
 *
 * @since 1.0
 * @author Mechite
 */
public class Latched<T> {

	private final CountDownLatch latch;
	private final AtomicReference<T> reference;

	/**
	 * Constructor that instantiates without setting an initial value.
	 *
	 * @see Latched#Latched(Object)
	 * @since 1.0
	 */
	public Latched() {
		this.latch = new CountDownLatch(1);
		this.reference = new AtomicReference<>();
	}

	/**
	 * Constructor that sets an initial value while keeping the latch
	 * closed. {@link Latched#release()} should not be called directly
	 * after the instantiation of this object (it is an anti-pattern to
	 * try and use this as a monadic type).
	 *
	 * @see Latched#Latched(Object)
	 * @since 1.0
	 */
	public Latched(T object) {
		this.latch = new CountDownLatch(1);
		this.reference = new AtomicReference<>(object);
	}

	/**
	 * Retrieves the locked object, awaiting release if it is still locked.
	 * This method is entirely thread-safe and can be called from anywhere.
	 *
	 * @throws RuntimeException Thrown given that an {@link InterruptedException}
	 * is thrown while attempting to run {@link CountDownLatch#await()} to await
	 * the release of the latch.
	 * @since 1.0
	 */
	public T get() {
		try {
			this.latch.await();
			return reference.get();
		} catch (InterruptedException exception) {
			throw new RuntimeException("Failed to await release of locked object", exception);
		}
	}

	/**
	 * Sets the locked object to a new value.
	 * This method is entirely thread-safe and can be called from anywhere.
	 *
	 * @since 1.0
	 */
	public void set(T object) {
		this.reference.set(object);
	}

	/**
	 * Releases the lock and allows the object to be retrieved.
	 * This method is entirely thread-safe and can be called from anywhere.
	 *
	 * @since 1.0
	 */
	public void release() {
		this.latch.countDown();
	}

	/**
	 * Returns whether the object is locked or not.
	 * This method is entirely thread-safe and can be called from anywhere.
	 * <p>
	 * This is generally only used for debugging or testing purposes, and it is
	 * not recommended that this is used directly; it is an anti-pattern as the
	 * {@link Latched#get()} and {@link Latched#set(Object)} methods are the only
	 * ones that should need to be used. If any, more involved functionality is
	 * desired, a {@link CountDownLatch} should be used directly against an object
	 * or primitive, or other different synchronization strategy.
	 *
	 * @see CountDownLatch
	 * @since 1.0
	 */
	public boolean locked() {
		return this.latch.getCount() != 0;
	}

	@Override
	public String toString() {
		return format("Latched[object={0}]", String.valueOf(this.reference.get()));
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null) return false;
		if (!this.locked() && object instanceof Latched<?> latched && !latched.locked()) {
			Object inner = latched.get();
			return Objects.deepEquals(this.reference.get(), inner);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.reference.get());
	}
}