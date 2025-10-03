package sirius.stellar.facility.tuple;

import org.jetbrains.annotations.Contract;
import sirius.stellar.facility.Iterators;
import sirius.stellar.facility.Orderable;
import sirius.stellar.facility.doctation.Internal;
import sirius.stellar.facility.exception.ImmutableModificationException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;

import static sirius.stellar.facility.Strings.*;

/**
 * A tuple consisting of three elements.
 * This class is non-sealed and may be extended for use as an abstraction.
 * <p>
 * Factory methods {@link Triplet#immutableTriplet} and {@link Triplet#mutableTriplet}
 * are available to create instances of the appropriate subtype. They are designed to
 * be imported statically to achieve a fluent interface.
 * <p>
 * A usage exemplar is as follows:
 * <pre>{@code
 *     // The `var` keyword can be used instead.
 *     // var triplet = immutableTriplet("Random", 16, 2007);
 *     Triplet<String, Integer, Integer> triplet = immutableTriplet("Random", 16, 2007);
 *
 *     triplet.first().equals("Random")
 *     triplet.second() == 16;
 *     triplet.third() == 2007;
 * }</pre>
 *
 * @since 1.0
 * @author Mechite
 */
public abstract class Triplet<A, B, C> implements Orderable<Triplet<A, B, C>>, Iterable<Object>, Serializable {

	@Serial
	private static final long serialVersionUID = 4529072832431752049L;

	//#region Factory Methods
	/**
	 * Creates an immutable triplet for three objects.
	 * @since 1.0
	 */
	@Contract("_, _, _ -> new")
	public static <A, B, C> Triplet<A, B, C> immutableTriplet(A first, B second, C third) {
		return new ImmutableTriplet<>(first, second, third);
	}

	/**
	 * Creates a mutable triplet for three objects.
	 * @since 1.0
	 */
	@Contract("_, _, _ -> new")
	public static <A, B, C> Triplet<A, B, C> mutableTriplet(A first, B second, C third) {
		return new MutableTriplet<>(first, second, third);
	}
	//#endregion

	//#region Abstract Methods
	/**
	 * Gets the first element in this triplet.
	 * @since 1.0
	 */
	public abstract A first();

	/**
	 * Gets the second element in this triplet.
	 * @since 1.0
	 */
	public abstract B second();

	/**
	 * Gets the third element in this triplet.
	 * @since 1.0
	 */
	public abstract C third();

	/**
	 * Sets the first element in this triplet.
	 * If the triplet is immutable, this method will throw {@link ImmutableModificationException}.
	 *
	 * @return The old value of the first element.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public abstract A first(A first);

	/**
	 * Sets the second element in this triplet.
	 * If the triplet is immutable, this method will throw {@link ImmutableModificationException}.
	 *
	 * @return The old value of the second element.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public abstract B second(B second);

	/**
	 * Sets the third element in this triplet.
	 * If the triplet is immutable, this method will throw {@link ImmutableModificationException}.
	 *
	 * @return The old value of the third element.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public abstract C third(C third);
	//#endregion

	//#region compare(), iterator(), equals(), hashCode() & toString() implementation.
	@Override
	public void compare(Triplet<A, B, C> other, Results results) {
		results.append(this.first(), other.first());
		results.append(this.second(), other.second());
		results.append(this.third(), other.third());
	}

	@Override
	public Iterator<Object> iterator() {
		return Iterators.from(this.first(), this.second(), this.third());
	}

	@Override
	public boolean equals(Object object) {
		return (object == this) || (object instanceof Triplet<?, ?, ?> triplet) && Objects.equals(this.first(), triplet.first()) && Objects.equals(this.second(), triplet.second()) && Objects.equals(this.third(), triplet.third());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.first(), this.second(), this.third());
	}

	@Override
	public String toString() {
		if (this instanceof MutableTriplet<A, B, C>) return format("MutableTriplet[{0}, {1}, {2}]", this.first(), this.second(), this.third());
		if (this instanceof ImmutableTriplet<A, B, C>) return format("ImmutableTriplet[{0}, {1}, {2}]", this.first(), this.second(), this.third());
		return format("Triplet[{0}, {1}, {2}]", this.first(), this.second(), this.third());
	}
	//#endregion
}

/**
 * A mutable implementation of {@link Triplet}.
 */
@Internal
final class MutableTriplet<A, B, C> extends Triplet<A, B, C> {

	@Serial
	private static final long serialVersionUID = 4529072832431752049L;

	private A first;
	private B second;
	private C third;

	MutableTriplet(A first, B second, C third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	@Override
	public A first() {
		return this.first;
	}

	@Override
	public B second() {
		return this.second;
	}

	@Override
	public C third() {
		return this.third;
	}

	@Override
	public A first(A first) {
		A old = this.first;
		this.first = first;
		return old;
	}

	@Override
	public B second(B second) {
		B old = this.second;
		this.second = second;
		return old;
	}

	@Override
	public C third(C third) {
		C old = this.third;
		this.third = third;
		return old;
	}
}

/**
 * An immutable implementation of {@link Triplet}.
 */
@Internal
final class ImmutableTriplet<A, B, C> extends Triplet<A, B, C> {

	@Serial
	private static final long serialVersionUID = 4529072832431752049L;

	private final A first;
	private final B second;
	private final C third;

	ImmutableTriplet(A first, B second, C third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	@Override
	public A first() {
		return this.first;
	}

	@Override
	public B second() {
		return this.second;
	}

	@Override
	public C third() {
		return this.third;
	}

	@Override
	public A first(A first) {
		throw new ImmutableModificationException();
	}

	@Override
	public B second(B second) {
		throw new ImmutableModificationException();
	}

	@Override
	public C third(C third) {
		throw new ImmutableModificationException();
	}
}