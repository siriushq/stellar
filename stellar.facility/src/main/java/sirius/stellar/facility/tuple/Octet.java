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

/// A tuple consisting of eight elements.
/// This class is non-sealed and may be extended for use as an abstraction.
///
/// Factory methods [#immutableOctet] and [#mutableOctet] are available to
/// create instances of the appropriate subtype. They are designed to
/// be imported statically to achieve a fluent interface.
///
/// Usage exemplars are available for tuples with &le;4 elements for brevity.
/// See [Couple], [Triplet], [Quartet] for example usage.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public abstract class Octet<A, B, C, D, E, F, G, H> implements Orderable<Octet<A, B, C, D, E, F, G, H>>, Iterable<Object>, Serializable {

	@Serial
	private static final long serialVersionUID = 3723717904972880012L;

	//#region Factory Methods
	/// Creates an immutable octet for four objects.
	/// @since 1.0
	@Contract("_, _, _, _, _, _, _, _ -> new")
	public static <A, B, C, D, E, F, G, H> Octet<A, B, C, D, E, F, G, H> immutableOctet(A first, B second, C third, D fourth, E fifth, F sixth, G seventh, H eighth) {
		return new ImmutableOctet<>(first, second, third, fourth, fifth, sixth, seventh, eighth);
	}

	/// Creates a mutable octet for four objects.
	/// @since 1.0
	@Contract("_, _, _, _, _, _, _, _ -> new")
	public static <A, B, C, D, E, F, G, H> Octet<A, B, C, D, E, F, G, H> mutableOctet(A first, B second, C third, D fourth, E fifth, F sixth, G seventh, H eighth) {
		return new MutableOctet<>(first, second, third, fourth, fifth, sixth, seventh, eighth);
	}
	//#endregion

	//#region Abstract Methods
	/// Gets the first element in this octet.
	/// @since 1.0
	public abstract A first();

	/// Gets the second element in this octet.
	/// @since 1.0
	public abstract B second();

	/// Gets the third element in this octet.
	/// @since 1.0
	public abstract C third();

	/// Gets the fourth element in this octet.
	/// @since 1.0
	public abstract D fourth();

	/// Gets the fifth element in this octet.
	/// @since 1.0
	public abstract E fifth();

	/// Gets the sixth element in this octet.
	/// @since 1.0
	public abstract F sixth();

	/// Gets the seventh element in this octet.
	/// @since 1.0
	public abstract G seventh();

	/// Gets the eighth element in this octet.
	/// @since 1.0
	public abstract H eighth();

	/// Sets the first element in this octet.
	/// If the octet is immutable, this method will throw [ImmutableModificationException].
	///
	/// @return The old value of the first element.
	/// @since 1.0
	@Contract("_ -> new")
	public abstract A first(A first);

	/// Sets the second element in this octet.
	/// If the octet is immutable, this method will throw [ImmutableModificationException].
	///
	/// @return The old value of the second element.
	/// @since 1.0
	@Contract("_ -> new")
	public abstract B second(B second);

	/// Sets the third element in this octet.
	/// If the octet is immutable, this method will throw [ImmutableModificationException].
	///
	/// @return The old value of the third element.
	/// @since 1.0
	@Contract("_ -> new")
	public abstract C third(C third);

	/// Sets the fourth element in this octet.
	/// If the octet is immutable, this method will throw [ImmutableModificationException].
	///
	/// @return The old value of the fourth element.
	/// @since 1.0
	@Contract("_ -> new")
	public abstract D fourth(D fourth);

	/// Sets the fifth element in this octet.
	/// If the octet is immutable, this method will throw [ImmutableModificationException].
	///
	/// @return The old value of the fifth element.
	/// @since 1.0
	@Contract("_ -> new")
	public abstract E fifth(E fifth);

	/// Sets the sixth element in this octet.
	/// If the octet is immutable, this method will throw [ImmutableModificationException].
	///
	/// @return The old value of the sixth element.
	/// @since 1.0
	@Contract("_ -> new")
	public abstract F sixth(F sixth);

	/// Sets the seventh element in this octet.
	/// If the octet is immutable, this method will throw [ImmutableModificationException].
	///
	/// @return The old value of the seventh element.
	/// @since 1.0
	@Contract("_ -> new")
	public abstract G seventh(G seventh);

	/// Sets the eighth element in this octet.
	/// If the octet is immutable, this method will throw [ImmutableModificationException].
	///
	/// @return The old value of the eighth element.
	/// @since 1.0
	@Contract("_ -> new")
	public abstract H eighth(H eighth);
	//#endregion

	//#region compare(), iterator(), equals(), hashCode() & toString() implementation.
	@Override
	public void compare(Octet<A, B, C, D, E, F, G, H> other, Results results) {
		results.append(this.first(), other.first());
		results.append(this.second(), other.second());
		results.append(this.third(), other.third());
		results.append(this.fourth(), other.fourth());
		results.append(this.fifth(), other.fifth());
		results.append(this.sixth(), other.sixth());
		results.append(this.seventh(), other.seventh());
		results.append(this.eighth(), other.eighth());
	}

	@Override
	public Iterator<Object> iterator() {
		return Iterators.from(this.first(), this.second(), this.third(), this.fourth(), this.fifth(), this.sixth(), this.seventh());
	}

	@Override
	public boolean equals(Object object) {
		return (object == this) || (object instanceof Octet<?, ?, ?, ?, ?, ?, ?, ?> octet) && Objects.equals(this.first(), octet.first()) && Objects.equals(this.second(), octet.second()) && Objects.equals(this.third(), octet.third()) && Objects.equals(this.fourth(), octet.fourth()) && Objects.equals(this.fifth(), octet.fifth()) && Objects.equals(this.sixth(), octet.sixth()) && Objects.equals(this.seventh(), octet.seventh()) && Objects.equals(this.eighth(), octet.eighth());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.first(), this.second(), this.third(), this.fourth(), this.fifth(), this.sixth(), this.seventh(), this.eighth());
	}

	@Override
	public String toString() {
		if (this instanceof MutableOctet<A, B, C, D, E, F, G, H>) return format("MutableOctet[{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}]", this.first(), this.second(), this.third(), this.fourth(), this.fifth(), this.sixth(), this.seventh(), this.eighth());
		if (this instanceof ImmutableOctet<A, B, C, D, E, F, G, H>) return format("ImmutableOctet[{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}]", this.first(), this.second(), this.third(), this.fourth(), this.fifth(), this.sixth(), this.seventh(), this.eighth());
		return format("Octet[{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}]", this.first(), this.second(), this.third(), this.fourth(), this.fifth(), this.sixth(), this.seventh(), this.eighth());
	}
	//#endregion
}

/// A mutable implementation of [Octet].
@Internal
final class MutableOctet<A, B, C, D, E, F, G, H> extends Octet<A, B, C, D, E, F, G, H> {

	@Serial
	private static final long serialVersionUID = 3723717904972880012L;

	private A first;
	private B second;
	private C third;
	private D fourth;
	private E fifth;
	private F sixth;
	private G seventh;
	private H eighth;

	MutableOctet(A first, B second, C third, D fourth, E fifth, F sixth, G seventh, H eighth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.fifth = fifth;
		this.sixth = sixth;
		this.seventh = seventh;
		this.eighth = eighth;
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
	public H eighth() {
		return this.eighth;
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

	@Override
	public H eighth(H eighth) {
		H old = this.eighth;
		this.eighth = eighth;
		return old;
	}
}

/// An immutable implementation of [Octet].
@Internal
final class ImmutableOctet<A, B, C, D, E, F, G, H> extends Octet<A, B, C, D, E, F, G, H> {

	@Serial
	private static final long serialVersionUID = 3723717904972880012L;

	private final A first;
	private final B second;
	private final C third;
	private final D fourth;
	private final E fifth;
	private final F sixth;
	private final G seventh;
	private final H eighth;

	ImmutableOctet(A first, B second, C third, D fourth, E fifth, F sixth, G seventh, H eighth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.fifth = fifth;
		this.sixth = sixth;
		this.seventh = seventh;
		this.eighth = eighth;
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
	public H eighth() {
		return this.eighth;
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

	@Override
	public H eighth(H eighth) {
		throw new ImmutableModificationException();
	}
}