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
 * A tuple consisting of five elements.
 * This class is non-sealed and may be extended for use as an abstraction.
 * <p>
 * Factory methods {@link Quintet#immutableQuintet} and {@link Quintet#mutableQuintet}
 * are available to create instances of the appropriate subtype. They are designed to
 * be imported statically to achieve a fluent interface.
 * <p>
 * Usage exemplars are available for tuples with &le;4 elements for brevity.
 * See {@link Couple}, {@link Triplet}, {@link Quartet} for example usage.
 *
 * @since 1.0
 * @author Mechite
 */
public abstract class Quintet<A, B, C, D, E> implements Orderable<Quintet<A, B, C, D, E>>, Iterable<Object>, Serializable {

	@Serial
	private static final long serialVersionUID = 1356093815330551527L;

	//#region Factory Methods
	/**
	 * Creates an immutable quintet for four objects.
	 * @since 1.0
	 */
	@Contract("_, _, _, _, _ -> new")
	public static <A, B, C, D, E> Quintet<A, B, C, D, E> immutableQuintet(A first, B second, C third, D fourth, E fifth) {
		return new ImmutableQuintet<>(first, second, third, fourth, fifth);
	}

	/**
	 * Creates a mutable quintet for four objects.
	 * @since 1.0
	 */
	@Contract("_, _, _, _, _ -> new")
	public static <A, B, C, D, E> Quintet<A, B, C, D, E> mutableQuintet(A first, B second, C third, D fourth, E fifth) {
		return new MutableQuintet<>(first, second, third, fourth, fifth);
	}
	//#endregion

	//#region Abstract Methods
	/**
	 * Gets the first element in this quintet.
	 * @since 1.0
	 */
	public abstract A first();

	/**
	 * Gets the second element in this quintet.
	 * @since 1.0
	 */
	public abstract B second();

	/**
	 * Gets the third element in this quintet.
	 * @since 1.0
	 */
	public abstract C third();

	/**
	 * Gets the fourth element in this quintet.
	 * @since 1.0
	 */
	public abstract D fourth();

	/**
	 * Gets the fifth element in this quintet.
	 * @since 1.0
	 */
	public abstract E fifth();

	/**
	 * Sets the first element in this quintet.
	 * If the quintet is immutable, this method will throw {@link ImmutableModificationException}.
	 *
	 * @return The old value of the first element.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public abstract A first(A first);

	/**
	 * Sets the second element in this quintet.
	 * If the quintet is immutable, this method will throw {@link ImmutableModificationException}.
	 *
	 * @return The old value of the second element.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public abstract B second(B second);

	/**
	 * Sets the third element in this quintet.
	 * If the quintet is immutable, this method will throw {@link ImmutableModificationException}.
	 *
	 * @return The old value of the third element.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public abstract C third(C third);

	/**
	 * Sets the fourth element in this quintet.
	 * If the quintet is immutable, this method will throw {@link ImmutableModificationException}.
	 *
	 * @return The old value of the fourth element.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public abstract D fourth(D fourth);

	/**
	 * Sets the fifth element in this quintet.
	 * If the quintet is immutable, this method will throw {@link ImmutableModificationException}.
	 *
	 * @return The old value of the fifth element.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public abstract E fifth(E fifth);
	//#endregion

	//#region compare(), iterator(), equals(), hashCode() & toString() implementation.
	@Override
	public void compare(Quintet<A, B, C, D, E> other, Results results) {
		results.append(this.first(), other.first());
		results.append(this.second(), other.second());
		results.append(this.third(), other.third());
		results.append(this.fourth(), other.fourth());
		results.append(this.fifth(), other.fifth());
	}

	@Override
	public Iterator<Object> iterator() {
		return Iterators.from(this.first(), this.second(), this.third(), this.fourth(), this.fifth());
	}

	@Override
	public boolean equals(Object object) {
		return (object == this) || (object instanceof Quintet<?, ?, ?, ?, ?> quintet) && Objects.equals(this.first(), quintet.first()) && Objects.equals(this.second(), quintet.second()) && Objects.equals(this.third(), quintet.third()) && Objects.equals(this.fourth(), quintet.fourth()) && Objects.equals(this.fifth(), quintet.fifth());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.first(), this.second(), this.third(), this.fourth(), this.fifth());
	}

	@Override
	public String toString() {
		if (this instanceof MutableQuintet<A, B, C, D, E>) return format("MutableQuintet[{0}, {1}, {2}, {3}, {4}]", this.first(), this.second(), this.third(), this.fourth(), this.fifth());
		if (this instanceof ImmutableQuintet<A, B, C, D, E>) return format("ImmutableQuintet[{0}, {1}, {2}, {3}, {4}]", this.first(), this.second(), this.third(), this.fourth(), this.fifth());
		return format("Quintet[{0}, {1}, {2}, {3}, {4}]", this.first(), this.second(), this.third(), this.fourth(), this.fifth());
	}
	//#endregion
}

/**
 * A mutable implementation of {@link Quintet}.
 */
@Internal
final class MutableQuintet<A, B, C, D, E> extends Quintet<A, B, C, D, E> {

	@Serial
	private static final long serialVersionUID = 1356093815330551527L;

	private A first;
	private B second;
	private C third;
	private D fourth;
	private E fifth;

	MutableQuintet(A first, B second, C third, D fourth, E fifth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.fifth = fifth;
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
	public D fourth() {
		return this.fourth;
	}

	@Override
	public E fifth() {
		return this.fifth;
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

	@Override
	public D fourth(D fourth) {
		D old = this.fourth;
		this.fourth = fourth;
		return old;
	}

	@Override
	public E fifth(E fifth) {
		E old = this.fifth;
		this.fifth = fifth;
		return old;
	}
}

/**
 * An immutable implementation of {@link Quintet}.
 */
@Internal
final class ImmutableQuintet<A, B, C, D, E> extends Quintet<A, B, C, D, E> {

	@Serial
	private static final long serialVersionUID = 1356093815330551527L;

	private final A first;
	private final B second;
	private final C third;
	private final D fourth;
	private final E fifth;

	ImmutableQuintet(A first, B second, C third, D fourth, E fifth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.fifth = fifth;
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
	public D fourth() {
		return this.fourth;
	}

	@Override
	public E fifth() {
		return this.fifth;
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

	@Override
	public D fourth(D fourth) {
		throw new ImmutableModificationException();
	}

	@Override
	public E fifth(E fifth) {
		throw new ImmutableModificationException();
	}
}