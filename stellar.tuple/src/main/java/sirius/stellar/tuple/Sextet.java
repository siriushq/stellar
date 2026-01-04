package sirius.stellar.tuple;

import sirius.stellar.annotation.Contract;
import sirius.stellar.facility.Orderable;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

import static java.text.MessageFormat.format;

/// A tuple consisting of six elements.
/// This class is non-sealed and may be extended for use as an abstraction.
///
/// Factory methods [#immutableSextet] and [#mutableSextet] are available
/// to create instances of the appropriate subtype. They are designed to
/// be imported statically to achieve a fluent interface.
///
/// Usage exemplars are available for tuples with &le;4 elements for brevity.
/// See [Couple], [Triplet], [Quartet] for example usage.
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public abstract class Sextet<A, B, C, D, E, F> implements Orderable<Sextet<A, B, C, D, E, F>>, Iterable<Object>, Serializable {

	private static final long serialVersionUID = 2857946100715781746L;

	//#region Factory Methods
	/// Creates an immutable sextet for four objects.
	/// @since 1.0
	@Contract("_, _, _, _, _, _ -> new")
	public static <A, B, C, D, E, F> Sextet<A, B, C, D, E, F> immutableSextet(A first, B second, C third, D fourth, E fifth, F sixth) {
		return new ImmutableSextet<>(first, second, third, fourth, fifth, sixth);
	}

	/// Creates a mutable sextet for four objects.
	/// @since 1.0
	@Contract("_, _, _, _, _, _ -> new")
	public static <A, B, C, D, E, F> Sextet<A, B, C, D, E, F> mutableSextet(A first, B second, C third, D fourth, E fifth, F sixth) {
		return new MutableSextet<>(first, second, third, fourth, fifth, sixth);
	}
	//#endregion

	//#region Abstract Methods
	/// Gets the first element in this sextet.
	/// @since 1.0
	public abstract A first();

	/// Gets the second element in this sextet.
	/// @since 1.0
	public abstract B second();

	/// Gets the third element in this sextet.
	/// @since 1.0
	public abstract C third();

	/// Gets the fourth element in this sextet.
	/// @since 1.0
	public abstract D fourth();

	/// Gets the fifth element in this sextet.
	/// @since 1.0
	public abstract E fifth();

	/// Gets the sixth element in this sextet.
	/// @since 1.0
	public abstract F sixth();

	/// Sets the first element in this sextet.
	/// If the sextet is immutable, this method will throw [UnsupportedOperationException].
	///
	/// @return The old value of the first element.
	/// @since 1.0
	@Contract("_ -> new")
	public abstract A first(A first);

	/// Sets the second element in this sextet.
	/// If the sextet is immutable, this method will throw [UnsupportedOperationException].
	///
	/// @return The old value of the second element.
	/// @since 1.0
	@Contract("_ -> new")
	public abstract B second(B second);

	/// Sets the third element in this sextet.
	/// If the sextet is immutable, this method will throw [UnsupportedOperationException].
	///
	/// @return The old value of the third element.
	/// @since 1.0
	@Contract("_ -> new")
	public abstract C third(C third);

	/// Sets the fourth element in this sextet.
	/// If the sextet is immutable, this method will throw [UnsupportedOperationException].
	///
	/// @return The old value of the fourth element.
	/// @since 1.0
	@Contract("_ -> new")
	public abstract D fourth(D fourth);

	/// Sets the fifth element in this sextet.
	/// If the sextet is immutable, this method will throw [UnsupportedOperationException].
	///
	/// @return The old value of the fifth element.
	/// @since 1.0
	@Contract("_ -> new")
	public abstract E fifth(E fifth);

	/// Sets the sixth element in this sextet.
	/// If the sextet is immutable, this method will throw [UnsupportedOperationException].
	///
	/// @return The old value of the sixth element.
	/// @since 1.0
	@Contract("_ -> new")
	public abstract F sixth(F sixth);
	//#endregion

	//#region compare(), iterator(), equals(), hashCode() & toString() implementation.
	@Override
	public void compare(Sextet<A, B, C, D, E, F> other, Results results) {
		results.append(this.first(), other.first());
		results.append(this.second(), other.second());
		results.append(this.third(), other.third());
		results.append(this.fourth(), other.fourth());
		results.append(this.fifth(), other.fifth());
		results.append(this.sixth(), other.sixth());
	}

	@Override
	public Iterator<Object> iterator() {
		return Stream.of(this.first(), this.second(), this.third(), this.fourth(), this.fifth(), this.sixth()).iterator();
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof Sextet)) return false;

		Sextet<?, ?, ?, ?, ?, ?> sextet = (Sextet<?, ?, ?, ?, ?, ?>) object;
		return Objects.equals(this.first(), sextet.first())
			&& Objects.equals(this.second(), sextet.second())
			&& Objects.equals(this.third(), sextet.third())
			&& Objects.equals(this.fourth(), sextet.fourth())
			&& Objects.equals(this.fifth(), sextet.fifth())
			&& Objects.equals(this.sixth(), sextet.sixth());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.first(), this.second(), this.third(), this.fourth(), this.fifth(), this.sixth());
	}

	@Override
	public String toString() {
		if (this instanceof MutableSextet) return format("MutableSextet[{0}, {1}, {2}, {3}, {4}, {5}]", this.first(), this.second(), this.third(), this.fourth(), this.fifth(), this.sixth());
		if (this instanceof ImmutableSextet) return format("ImmutableSextet[{0}, {1}, {2}, {3}, {4}, {5}]", this.first(), this.second(), this.third(), this.fourth(), this.fifth(), this.sixth());
		return format("Sextet[{0}, {1}, {2}, {3}, {4}, {5}]", this.first(), this.second(), this.third(), this.fourth(), this.fifth(), this.sixth());
	}
	//#endregion
}

/// A mutable implementation of [Sextet].
final class MutableSextet<A, B, C, D, E, F> extends Sextet<A, B, C, D, E, F> {

	private static final long serialVersionUID = 2857946100715781746L;

	private A first;
	private B second;
	private C third;
	private D fourth;
	private E fifth;
	private F sixth;

	MutableSextet(A first, B second, C third, D fourth, E fifth, F sixth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.fifth = fifth;
		this.sixth = sixth;
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
}

/// An immutable implementation of [Sextet].
final class ImmutableSextet<A, B, C, D, E, F> extends Sextet<A, B, C, D, E, F> {

	private static final long serialVersionUID = 2857946100715781746L;

	private final A first;
	private final B second;
	private final C third;
	private final D fourth;
	private final E fifth;
	private final F sixth;

	ImmutableSextet(A first, B second, C third, D fourth, E fifth, F sixth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.fifth = fifth;
		this.sixth = sixth;
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
	public A first(A first) {
		throw new UnsupportedOperationException();
	}

	@Override
	public B second(B second) {
		throw new UnsupportedOperationException();
	}

	@Override
	public C third(C third) {
		throw new UnsupportedOperationException();
	}

	@Override
	public D fourth(D fourth) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E fifth(E fifth) {
		throw new UnsupportedOperationException();
	}

	@Override
	public F sixth(F sixth) {
		throw new UnsupportedOperationException();
	}
}