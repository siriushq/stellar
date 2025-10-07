package sirius.stellar.facility.tuple;

import org.jetbrains.annotations.Contract;
import sirius.stellar.facility.Iterators;
import sirius.stellar.facility.Orderable;
import sirius.stellar.facility.doctation.Internal;
import sirius.stellar.facility.exception.ImmutableModificationException;

import java.io.Serial;
import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import static sirius.stellar.facility.Strings.*;

/**
 * A tuple consisting of two elements (coupled together).
 * This class is non-sealed and may be extended for use as an abstraction.
 * <p>
 * Factory methods {@link Couple#immutableCouple} and {@link Couple#mutableCouple}
 * are available to create instances of the appropriate subtype. They are designed
 * to be imported statically to achieve a fluent interface.
 * <p>
 * A usage exemplar is as follows:
 * <pre>{@code
 *     // The `var` keyword can be used instead.
 *     // var couple = immutableCouple("Random", 16);
 *     Couple<String, Integer> couple = immutableCouple("Random", 16);
 *
 *     couple.first().equals("Random")
 *     couple.second() == 16;
 *
 *     // Couple conveniently implements Map.Entry as it contains just two elements.
 *     couple.getKey().equals("Random");
 *     couple.getValue() == 16;
 * }</pre>
 *
 * @author Mahied Maruf (mechite)
 * @since 1.0
 */
public abstract class Couple<A, B> implements Map.Entry<A, B>, Orderable<Couple<A, B>>, Iterable<Object>, Serializable {

	@Serial
	private static final long serialVersionUID = 2425620414811236114L;

	//#region Factory Methods
	/**
	 * Creates an immutable couple for two objects.
	 * @since 1.0
	 */
	@Contract("_, _ -> new")
	public static <A, B> Couple<A, B> immutableCouple(A first, B second) {
		return new ImmutableCouple<>(first, second);
	}

	/**
	 * Creates an immutable couple based on the provided {@link Map.Entry}.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public static <A, B> Couple<A, B> immutableCouple(Map.Entry<A, B> entry) {
		return new ImmutableCouple<>(entry.getKey(), entry.getValue());
	}

	/**
	 * Creates a mutable couple for two objects.
	 * @since 1.0
	 */
	@Contract("_, _ -> new")
	public static <A, B> Couple<A, B> mutableCouple(A first, B second) {
		return new MutableCouple<>(first, second);
	}

	/**
	 * Creates a mutable couple based on the provided {@link Map.Entry}.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public static <A, B> Couple<A, B> mutableCouple(Map.Entry<A, B> entry) {
		return new MutableCouple<>(entry.getKey(), entry.getValue());
	}
	//#endregion

	//#region Abstract Methods
	/**
	 * Gets the first element in this couple.
	 * @since 1.0
	 */
	public abstract A first();

	/**
	 * Gets the second element in this couple.
	 * @since 1.0
	 */
	public abstract B second();

	/**
	 * Sets the first element in this couple.
	 * If the couple is immutable, this method will throw {@link ImmutableModificationException}.
	 *
	 * @return The old value of the first element.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public abstract A first(A first);

	/**
	 * Sets the second element in this couple.
	 * If the couple is immutable, this method will throw {@link ImmutableModificationException}.
	 *
	 * @return The old value of the second element.
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public abstract B second(B second);
	//#endregion

	//#region Map.Entry Implementation
	/**
	 * Implementation of {@link Map.Entry#getKey()}.
	 * <p>
	 * This should not be preferred over {@link Couple#first()} and instead
	 * should only be used if compatibility with code using {@link Map.Entry}
	 * is desired, this is a very common thing to see when people are in need
	 * of a tuple implementation due to {@link SimpleEntry}'s existence, and
	 * is a design pitfall due to the unsuitability of that interface as a
	 * general purpose tuple.
	 *
	 * @see Couple#first()
	 */
	@Override
	public final A getKey() {
		return this.first();
	}

	/**
	 * Implementation of {@link Map.Entry#getValue()}.
	 * <p>
	 * This should not be preferred over {@link Couple#second()} and instead
	 * should only be used if compatibility with code using {@link Map.Entry}
	 * is desired, this is a very common thing to see when people are in need
	 * of a tuple implementation due to {@link SimpleEntry}'s existence, and
	 * is a design pitfall due to the unsuitability of that interface as a
	 * general purpose tuple.
	 *
	 * @see Couple#second()
	 */
	@Override
	public final B getValue() {
		return this.second();
	}

	/**
	 * Implementation of {@link Map.Entry#setValue(Object)}.
	 * <p>
	 * This should not be preferred over {@link Couple#first(Object)} and
	 * {@link Couple#second(Object)} and instead should only be used if
	 * compatibility with code using {@link Map.Entry} is desired, this
	 * is a very common thing to see when people are in need of a tuple
	 * implementation due to {@link SimpleEntry}'s existence, and is a
	 * design pitfall due to the unsuitability of that interface as a
	 * general purpose tuple.
	 *
	 * @see Couple#second(Object)
	 */
	@Override
	public final B setValue(B value) {
		B second = second();
		this.second(value);
		return second;
	}
	//#endregion

	//#region compare(), iterator(), equals(), hashCode() & toString() implementation.
	@Override
	public void compare(Couple<A, B> other, Results results) {
		results.append(this.first(), other.first());
		results.append(this.second(), other.second());
	}

	@Override
	public Iterator<Object> iterator() {
		return Iterators.from(this.first(), this.second());
	}

	@Override
	public boolean equals(Object object) {
		return (object == this) || (object instanceof Map.Entry<?, ?> entry) && Objects.equals(this.first(), entry.getKey()) && Objects.equals(this.second(), entry.getValue());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.first(), this.second());
	}

	@Override
	public String toString() {
		if (this instanceof MutableCouple<A, B>) return format("MutableCouple[{0}, {1}]", this.first(), this.second());
		if (this instanceof ImmutableCouple<A, B>) return format("ImmutableCouple[{0}, {1}]", this.first(), this.second());
		return format("Couple[{0}, {1}]", this.first(), this.second());
	}
	//#endregion
}

/**
 * A mutable implementation of {@link Couple}.
 */
@Internal
final class MutableCouple<A, B> extends Couple<A, B> {

	@Serial
	private static final long serialVersionUID = 2425620414811236114L;

	private A first;
	private B second;

	MutableCouple(A first, B second) {
		this.first = first;
		this.second = second;
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
}

/**
 * An immutable implementation of {@link Couple}.
 */
@Internal
final class ImmutableCouple<A, B> extends Couple<A, B> {

	@Serial
	private static final long serialVersionUID = 2425620414811236114L;

	private final A first;
	private final B second;

	ImmutableCouple(A first, B second) {
		this.first = first;
		this.second = second;
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
	public A first(A first) {
		throw new ImmutableModificationException();
	}

	@Override
	public B second(B second) {
		throw new ImmutableModificationException();
	}
}