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
 * A tuple consisting of seven elements.
 * This class is non-sealed and may be extended for use as an abstraction.
 * <p>
 * Factory methods {@link Septet#immutableSeptet} and {@link Septet#mutableSeptet}
 * are available to create instances of the appropriate subtype. They are designed to
 * be imported statically to achieve a fluent interface.
 * <p>
 * Usage exemplars are available for tuples with &le;4 elements for brevity.
 * See {@link Couple}, {@link Triplet}, {@link Quartet} for example usage.
 *
 * @author Mahied Maruf (mechite)
 * @since 1.0
 */
public abstract class Septet<A, B, C, D, E, F, G> implements Orderable<Septet<A, B, C, D, E, F, G>>, Iterable<Object>, Serializable {

	@Serial
	private static final long serialVersionUID = 3723717904972880012L;

	//#region Factory Methods
	/**
	 * Creates an immutable septet for four objects.
	 * @since 1.0
	 */
	@Contract("_, _, _, _, _, _, _ -> new")
	public static <A, B, C, D, E, F, G> Septet<A, B, C, D, E, F, G> immutableSeptet(A first, B second, C third, D fourth, E fifth, F sixth, G seventh) {
		return new ImmutableSeptet<>(first, second, third, fourth, fifth, sixth, seventh);
	}

	/**
	 * Creates a mutable septet for four objects.
	 * @since 1.0
	 */
	@Contract("_, _, _, _, _, _, _ -> new")
	public static <A, B, C, D, E, F, G> Septet<A, B, C, D, E, F, G> mutableSeptet(A first, B second, C third, D fourth, E fifth, F sixth, G seventh) {
		return new MutableSeptet<>(first, second, third, fourth, fifth, sixth, seventh);
	}
	//#endregion

	//#region Abstract Methods
	/**
	 * Gets the first element in this septet.
	 * @since 1.0
	 */
	public abstract A first();

	/**
	 * Gets the second element in this septet.
	 * @since 1.0
	 */
	public abstract B second();

	/**
	 * Gets the third element in this septet.
	 * @since 1.0
	 */
	public abstract C third();

	/**
	 * Gets the fourth element in this septet.
	 * @since 1.0
	 */
	public abstract D fourth();

	/**
	 * Gets the fifth element in this septet.
	 * @since 1.0
	 */
	public abstract E fifth();

	/**
	 * Gets the sixth element in this septet.
	 * @since 1.0
	 */
	public abstract F sixth();

	/**
	 * Gets the seventh element in this septet.
	 * @since 1.0
	 */
	public abstract G seventh();

	/**
	 * Sets the first element in this septet.
	 * If the septet is immutable, this method will throw {@link ImmutableModificationException}.
	 *
	 * @return The old value of the first element.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public abstract A first(A first);

	/**
	 * Sets the second element in this septet.
	 * If the septet is immutable, this method will throw {@link ImmutableModificationException}.
	 *
	 * @return The old value of the second element.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public abstract B second(B second);

	/**
	 * Sets the third element in this septet.
	 * If the septet is immutable, this method will throw {@link ImmutableModificationException}.
	 *
	 * @return The old value of the third element.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public abstract C third(C third);

	/**
	 * Sets the fourth element in this septet.
	 * If the septet is immutable, this method will throw {@link ImmutableModificationException}.
	 *
	 * @return The old value of the fourth element.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public abstract D fourth(D fourth);

	/**
	 * Sets the fifth element in this septet.
	 * If the septet is immutable, this method will throw {@link ImmutableModificationException}.
	 *
	 * @return The old value of the fifth element.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public abstract E fifth(E fifth);

	/**
	 * Sets the sixth element in this septet.
	 * If the septet is immutable, this method will throw {@link ImmutableModificationException}.
	 *
	 * @return The old value of the sixth element.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public abstract F sixth(F sixth);

	/**
	 * Sets the seventh element in this septet.
	 * If the septet is immutable, this method will throw {@link ImmutableModificationException}.
	 *
	 * @return The old value of the seventh element.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public abstract G seventh(G seventh);
	//#endregion

	//#region compare(), iterator(), equals(), hashCode() & toString() implementation.
	@Override
	public void compare(Septet<A, B, C, D, E, F, G> other, Results results) {
		results.append(this.first(), other.first());
		results.append(this.second(), other.second());
		results.append(this.third(), other.third());
		results.append(this.fourth(), other.fourth());
		results.append(this.fifth(), other.fifth());
		results.append(this.sixth(), other.sixth());
		results.append(this.seventh(), other.seventh());
	}

	@Override
	public Iterator<Object> iterator() {
		return Iterators.from(this.first(), this.second(), this.third(), this.fourth(), this.fifth(), this.sixth(), this.seventh());
	}

	@Override
	public boolean equals(Object object) {
		return (object == this) || (object instanceof Septet<?, ?, ?, ?, ?, ?, ?> septet) && Objects.equals(this.first(), septet.first()) && Objects.equals(this.second(), septet.second()) && Objects.equals(this.third(), septet.third()) && Objects.equals(this.fourth(), septet.fourth()) && Objects.equals(this.fifth(), septet.fifth()) && Objects.equals(this.sixth(), septet.sixth()) && Objects.equals(this.seventh(), septet.seventh());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.first(), this.second(), this.third(), this.fourth(), this.fifth(), this.sixth(), this.seventh());
	}

	@Override
	public String toString() {
		if (this instanceof MutableSeptet<A, B, C, D, E, F, G>) return format("MutableSeptet[{0}, {1}, {2}, {3}, {4}, {5}, {6}]", this.first(), this.second(), this.third(), this.fourth(), this.fifth(), this.sixth(), this.seventh());
		if (this instanceof ImmutableSeptet<A, B, C, D, E, F, G>) return format("ImmutableSeptet[{0}, {1}, {2}, {3}, {4}, {5}, {6}]", this.first(), this.second(), this.third(), this.fourth(), this.fifth(), this.sixth(), this.seventh());
		return format("Septet[{0}, {1}, {2}, {3}, {4}, {5}, {6}]", this.first(), this.second(), this.third(), this.fourth(), this.fifth(), this.sixth(), this.seventh());
	}
	//#endregion
}

/**
 * A mutable implementation of {@link Septet}.
 */
@Internal
final class MutableSeptet<A, B, C, D, E, F, G> extends Septet<A, B, C, D, E, F, G> {

	@Serial
	private static final long serialVersionUID = 3723717904972880012L;

	private A first;
	private B second;
	private C third;
	private D fourth;
	private E fifth;
	private F sixth;
	private G seventh;

	MutableSeptet(A first, B second, C third, D fourth, E fifth, F sixth, G seventh) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.fifth = fifth;
		this.sixth = sixth;
		this.seventh = seventh;
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
	public F sixth() {
		return this.sixth;
	}

	@Override
	public G seventh() {
		return this.seventh;
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

	@Override
	public F sixth(F sixth) {
		F old = this.sixth;
		this.sixth = sixth;
		return old;
	}

	@Override
	public G seventh(G seventh) {
		G old = this.seventh;
		this.seventh = seventh;
		return old;
	}
}

/**
 * An immutable implementation of {@link Septet}.
 */
@Internal
final class ImmutableSeptet<A, B, C, D, E, F, G> extends Septet<A, B, C, D, E, F, G> {

	@Serial
	private static final long serialVersionUID = 3723717904972880012L;

	private final A first;
	private final B second;
	private final C third;
	private final D fourth;
	private final E fifth;
	private final F sixth;
	private final G seventh;

	ImmutableSeptet(A first, B second, C third, D fourth, E fifth, F sixth, G seventh) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.fifth = fifth;
		this.sixth = sixth;
		this.seventh = seventh;
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
	public F sixth() {
		return this.sixth;
	}

	@Override
	public G seventh() {
		return this.seventh;
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

	@Override
	public F sixth(F sixth) {
		throw new ImmutableModificationException();
	}

	@Override
	public G seventh(G seventh) {
		throw new ImmutableModificationException();
	}
}