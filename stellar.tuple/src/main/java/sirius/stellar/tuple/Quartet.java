package sirius.stellar.tuple;

import sirius.stellar.annotation.Contract;
import sirius.stellar.facility.Orderable;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

import static java.text.MessageFormat.*;

/// A tuple consisting of four elements.
/// This class is non-sealed and may be extended for use as an abstraction.
///
/// Factory methods [#immutableQuartet] and [#mutableQuartet] are available
/// to create instances of the appropriate subtype. They are designed to
/// be imported statically to achieve a fluent interface.
///
/// A usage exemplar is as follows:
/// ```
/// // The `var` keyword can be used instead.
/// // var quartet = immutableQuartet("Random", 16, 2007, 175);
/// Quartet<String, Integer, Integer, Integer> quartet = immutableQuartet(
///     "Random",
///     16,
///     2007,
///     175
/// );
/// quartet.first().equals("Random")
/// quartet.second() == 16;
/// quartet.third() == 2007;
/// quartet.fourth() == 175;
/// ```
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public abstract class Quartet<A, B, C, D> implements Orderable<Quartet<A, B, C, D>>, Iterable<Object>, Serializable {

	private static final long serialVersionUID = 267215234138977650L;

	//#region Factory Methods
	/// Creates an immutable quartet for four objects.
	/// @since 1.0
	@Contract("_, _, _, _ -> new")
	public static <A, B, C, D> Quartet<A, B, C, D> immutableQuartet(A first, B second, C third, D fourth) {
		return new ImmutableQuartet<>(first, second, third, fourth);
	}

	/// Creates a mutable quartet for four objects.
	/// @since 1.0
	@Contract("_, _, _, _ -> new")
	public static <A, B, C, D> Quartet<A, B, C, D> mutableQuartet(A first, B second, C third, D fourth) {
		return new MutableQuartet<>(first, second, third, fourth);
	}
	//#endregion

	//#region Abstract Methods
	/// Gets the first element in this quartet.
	/// @since 1.0
	public abstract A first();

	/// Gets the second element in this quartet.
	/// @since 1.0
	public abstract B second();

	/// Gets the third element in this quartet.
	/// @since 1.0
	public abstract C third();

	/// Gets the fourth element in this quartet.
	/// @since 1.0
	public abstract D fourth();

	/// Sets the first element in this quartet.
	/// If the quartet is immutable, this method will throw [UnsupportedOperationException].
	///
	/// @return The old value of the first element.
	/// @since 1.0
	@Contract("_ -> new")
	public abstract A first(A first);

	/// Sets the second element in this quartet.
	/// If the quartet is immutable, this method will throw [UnsupportedOperationException].
	///
	/// @return The old value of the second element.
	/// @since 1.0
	@Contract("_ -> new")
	public abstract B second(B second);

	/// Sets the third element in this quartet.
	/// If the quartet is immutable, this method will throw [UnsupportedOperationException].
	///
	/// @return The old value of the third element.
	/// @since 1.0
	@Contract("_ -> new")
	public abstract C third(C third);

	/// Sets the fourth element in this quartet.
	/// If the quartet is immutable, this method will throw [UnsupportedOperationException].
	///
	/// @return The old value of the fourth element.
	/// @since 1.0
	@Contract("_ -> new")
	public abstract D fourth(D fourth);
	//#endregion

	//#region compare(), iterator(), equals(), hashCode() & toString() implementation.
	@Override
	public void compare(Quartet<A, B, C, D> other, Results results) {
		results.append(this.first(), other.first());
		results.append(this.second(), other.second());
		results.append(this.third(), other.third());
		results.append(this.fourth(), other.fourth());
	}

	@Override
	public Iterator<Object> iterator() {
		return Stream.of(this.first(), this.second(), this.third(), this.fourth()).iterator();
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof Quartet)) return false;

		Quartet<?, ?, ?, ?> quartet = (Quartet<?, ?, ?, ?>) object;
		return Objects.equals(this.first(), quartet.first())
			&& Objects.equals(this.second(), quartet.second())
			&& Objects.equals(this.third(), quartet.third())
			&& Objects.equals(this.fourth(), quartet.fourth());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.first(), this.second(), this.third(), this.fourth());
	}

	@Override
	public String toString() {
		if (this instanceof MutableQuartet) return format("MutableQuartet[{0}, {1}, {2}, {3}]", this.first(), this.second(), this.third(), this.fourth());
		if (this instanceof ImmutableQuartet) return format("ImmutableQuartet[{0}, {1}, {2}, {3}]", this.first(), this.second(), this.third(), this.fourth());
		return format("Quartet[{0}, {1}, {2}, {3}]", this.first(), this.second(), this.third(), this.fourth());
	}
	//#endregion
}

/// A mutable implementation of [Quartet].
final class MutableQuartet<A, B, C, D> extends Quartet<A, B, C, D> {

	private static final long serialVersionUID = 267215234138977650L;

	private A first;
	private B second;
	private C third;
	private D fourth;

	MutableQuartet(A first, B second, C third, D fourth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
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
}

/// An immutable implementation of [Quartet].
final class ImmutableQuartet<A, B, C, D> extends Quartet<A, B, C, D> {

	private static final long serialVersionUID = 267215234138977650L;

	private final A first;
	private final B second;
	private final C third;
	private final D fourth;

	ImmutableQuartet(A first, B second, C third, D fourth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
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
}