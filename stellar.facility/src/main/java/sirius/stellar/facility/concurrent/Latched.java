package sirius.stellar.facility.concurrent;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static sirius.stellar.facility.Strings.*;

/// A wrapper around a given object of type `T`, with methods to wait until it is available.
///
/// This is thread-safe, and the [#set(Object)] method can be invoked from any thread; once
/// [#release()] has been invoked, [#get()] will return on every thread waiting for it, even
/// if the object is `null`; the control on when the object is available is separate from the
/// actual assignment of the object.
///
/// [#toString()] returns a message that contains the locked object, whether the latch has been
/// released or not — it can potentially display `"null"`, but is entirely null-safe and uses
/// [String#valueOf(Object)] in order to obtain this value. The message will appear as
/// `Latched[object=*]`, where `*` is `String.valueOf(latched.reference.get())`.
///
/// [#hashCode()] returns a hash code for the latched object if it has been set, or zero if it
/// has not (as [Objects#hashCode(Object)] is called).
///
/// [#equals(Object)] will return true if this latched object is equal to itself, false if the
/// object it is compared to is `null`, or true if the object it is compared to is another
/// latched object where the set object is deeply equal to this latched object's set object. The
/// deep equality is calculated with [Objects#deepEquals(Object, Object)]. If the latched object
/// is still locked, it will instead return false rather than trying to await the object's release,
/// as that qualifies as lack of equality.
///
/// This is checked with [#locked()], which in turn invokes [CountDownLatch#getCount()], which may
/// not be desirable behaviour.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public class Latched<T> {

	private final CountDownLatch latch;
	private final AtomicReference<T> reference;

	/// Constructor that instantiates without setting an initial value.
	///
	/// @see Latched#Latched(Object)
	/// @since 1.0
	public Latched() {
		this.latch = new CountDownLatch(1);
		this.reference = new AtomicReference<>();
	}

	/// Constructor that sets an initial value while keeping the latch
	/// closed. [#release()] should not be called directly after the
	/// instantiation of this object (it is an anti-pattern to try
	/// and use this as a monadic type).
	///
	/// @see Latched#Latched(Object)
	/// @since 1.0
	public Latched(T object) {
		this.latch = new CountDownLatch(1);
		this.reference = new AtomicReference<>(object);
	}

	/// Retrieves the locked object, awaiting release if it is still locked.
	/// This method is entirely thread-safe and can be called from anywhere.
	///
	/// @throws RuntimeException Thrown given that an [InterruptedException]
	/// is thrown while attempting to run [#await()] to await
	/// the release of the latch.
	/// @since 1.0
	public T get() {
		try {
			this.latch.await();
			return reference.get();
		} catch (InterruptedException exception) {
			throw new RuntimeException("Failed to await release of locked object", exception);
		}
	}

	/// Sets the locked object to a new value.
	/// This method is entirely thread-safe and can be called from anywhere.
	///
	/// @since 1.0
	public void set(T object) {
		this.reference.set(object);
	}

	/// Releases the lock and allows the object to be retrieved.
	/// This method is entirely thread-safe and can be called from anywhere.
	///
	/// @since 1.0
	public void release() {
		this.latch.countDown();
	}

	/// Returns whether the object is locked or not.
	/// This method is entirely thread-safe and can be called from anywhere.
	///
	/// This is generally only used for debugging or testing purposes, and it is not
	/// recommended that this is used directly; it is an anti-pattern as the [#get()]
	/// and [#set(Object)] methods are the only ones that should need to be used.
	/// If any, more involved functionality is desired, a [CountDownLatch] should be
	/// used directly against an object or primitive, or other different
	/// synchronization strategy.
	///
	/// @see CountDownLatch
	/// @since 1.0
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