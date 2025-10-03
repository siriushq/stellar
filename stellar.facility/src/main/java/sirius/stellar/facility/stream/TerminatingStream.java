package sirius.stellar.facility.stream;

import org.jetbrains.annotations.Contract;

import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.Path;
import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.*;

/**
 * Implementation of {@link Stream} which will automatically close when a terminal
 * operation is executed. While the majority of {@link Stream}s do not have to be
 * (and should not be) explicitly closed, the following methods provide IO streams
 * (and several third party libraries may contain similar IO streams) which need to
 * be closed explicitly:
 * <ul>
 *     <li>{@link java.nio.file.Files#list(Path)}</li>
 *     <li>{@link java.nio.file.Files#walk(Path, FileVisitOption...)}</li>
 *     <li>{@link java.nio.file.Files#walk(Path, int, FileVisitOption...)}</li>
 *     <li>{@link java.nio.file.Files#find(Path, int, BiPredicate, FileVisitOption...)}</li>
 *     <li>{@link java.nio.file.Files#lines(Path)}</li>
 *     <li>{@link java.nio.file.Files#lines(Path, Charset)}</li>
 *     <li>{@link java.util.Scanner#tokens()}</li>
 *     <li>{@link java.util.Scanner#findAll(Pattern)}</li>
 *     <li>{@link java.util.Scanner#findAll(String)}</li>
 * </ul>
 * There may be more standard methods that have not been mentioned here, as well as
 * new methods introduced in the future. However, these are the most commonly used.
 * <p>
 * The motivation for creating such a utility is that the use of try-with-resources
 * in this situation is not the best choice.
 * <p>
 * While with try-with-resources, we get the simplest implementation as of now, if
 * we were to close the stream without it, one would not pick try-finally. Instead,
 * one would store the stream, perform the terminal operation, and close it right
 * afterward. It is more pragmatic to close on a terminal operation, as the stream
 * is still entirely useless, however, the IO lock remains.
 * <p>
 * With the use of this utility, one is able to completely avoid storing the stream
 * explicitly, as well as avoid creating a new scope with try-with-resources. This
 * can provide a major concision.
 * <p>
 * The factory method {@link TerminatingStream#terminalStream(Stream)} is available,
 * designed to be imported statically for a fluent interface. A usage exemplar is
 * as follows:
 * <pre>{@code
 *     List<String> lines = terminalStream(Files.lines(...))
 *             .filter(...)
 *             .toList(); // This invocation will close the stream.
 *
 *     terminalStream(Files.list(...))
 *             .filter(...)
 *             .forEach(path -> ...); // This invocation will close the stream.
 * }</pre>
 *
 * @since 1.0
 * @author Mechite
 */
public final class TerminatingStream<T> implements Stream<T> {

	private final Stream<T> stream;

	private TerminatingStream(Stream<T> stream) {
		this.stream = stream;
	}

	/**
	 * Creates an auto terminating stream that wraps the provided stream.
	 * This will close the stream when a terminal operation is performed.
	 *
	 * @since 1.0
	 */
	@Contract("_ -> new")
	public static <T> TerminatingStream<T> terminalStream(Stream<T> stream) {
		return new TerminatingStream<>(stream);
	}

	//#region Terminal Operations
	@Override
	public void forEach(Consumer<? super T> action) {
		this.stream.forEach(action);
		this.stream.close();
	}

	@Override
	public void forEachOrdered(Consumer<? super T> action) {
		this.stream.forEachOrdered(action);
		this.stream.close();
	}

	@Override
	public Object[] toArray() {
		Object[] array = this.stream.toArray();
		this.stream.close();
		return array;
	}

	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		A[] array = this.stream.toArray(generator);
		this.stream.close();
		return array;
	}

	@Override
	public T reduce(T identity, BinaryOperator<T> accumulator) {
		T reduce = this.stream.reduce(identity, accumulator);
		this.stream.close();
		return reduce;
	}

	@Override
	public Optional<T> reduce(BinaryOperator<T> accumulator) {
		Optional<T> optional = this.stream.reduce(accumulator);
		this.stream.close();
		return optional;
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
		U reduce = this.stream.reduce(identity, accumulator, combiner);
		this.stream.close();
		return reduce;
	}

	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
		R collect = this.stream.collect(supplier, accumulator, combiner);
		this.stream.close();
		return collect;
	}

	@Override
	public <R, A> R collect(Collector<? super T, A, R> collector) {
		R collect = this.stream.collect(collector);
		this.stream.close();
		return collect;
	}

	@Override
	public List<T> toList() {
		List<T> list = this.stream.toList();
		this.stream.close();
		return list;
	}

	@Override
	public Optional<T> min(Comparator<? super T> comparator) {
		Optional<T> optional = this.stream.min(comparator);
		this.stream.close();
		return optional;
	}

	@Override
	public Optional<T> max(Comparator<? super T> comparator) {
		Optional<T> optional = this.stream.max(comparator);
		this.stream.close();
		return optional;
	}

	@Override
	public long count() {
		long count = this.stream.count();
		this.stream.close();
		return count;
	}

	@Override
	public boolean anyMatch(Predicate<? super T> predicate) {
		boolean anyMatch = this.stream.anyMatch(predicate);
		this.stream.close();
		return anyMatch;
	}

	@Override
	public boolean allMatch(Predicate<? super T> predicate) {
		boolean allMatch = this.stream.allMatch(predicate);
		this.stream.close();
		return allMatch;
	}

	@Override
	public boolean noneMatch(Predicate<? super T> predicate) {
		boolean noneMatch = this.stream.noneMatch(predicate);
		this.stream.close();
		return noneMatch;
	}

	@Override
	public Optional<T> findFirst() {
		Optional<T> optional = this.stream.findFirst();
		this.stream.close();
		return optional;
	}

	@Override
	public Optional<T> findAny() {
		Optional<T> optional = this.stream.findAny();
		this.stream.close();
		return optional;
	}

	@Override
	public Iterator<T> iterator() {
		Iterator<T> iterator = this.stream.iterator();
		this.stream.close();
		return iterator;
	}

	@Override
	public Spliterator<T> spliterator() {
		Spliterator<T> spliterator = this.stream.spliterator();
		this.stream.close();
		return spliterator;
	}
	//#endregion

	//#region Other Operations [direct delegates]
	@Override
	public Stream<T> filter(Predicate<? super T> predicate) {
		return this.stream.filter(predicate);
	}

	@Override
	public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
		return this.stream.map(mapper);
	}

	@Override
	public IntStream mapToInt(ToIntFunction<? super T> mapper) {
		return this.stream.mapToInt(mapper);
	}

	@Override
	public LongStream mapToLong(ToLongFunction<? super T> mapper) {
		return this.stream.mapToLong(mapper);
	}

	@Override
	public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
		return this.stream.mapToDouble(mapper);
	}

	@Override
	public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
		return this.stream.flatMap(mapper);
	}

	@Override
	public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
		return this.stream.flatMapToInt(mapper);
	}

	@Override
	public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
		return this.stream.flatMapToLong(mapper);
	}

	@Override
	public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
		return this.stream.flatMapToDouble(mapper);
	}

	@Override
	public <R> Stream<R> mapMulti(BiConsumer<? super T, ? super Consumer<R>> mapper) {
		return this.stream.mapMulti(mapper);
	}

	@Override
	public IntStream mapMultiToInt(BiConsumer<? super T, ? super IntConsumer> mapper) {
		return this.stream.mapMultiToInt(mapper);
	}

	@Override
	public LongStream mapMultiToLong(BiConsumer<? super T, ? super LongConsumer> mapper) {
		return this.stream.mapMultiToLong(mapper);
	}

	@Override
	public DoubleStream mapMultiToDouble(BiConsumer<? super T, ? super DoubleConsumer> mapper) {
		return this.stream.mapMultiToDouble(mapper);
	}

	@Override
	public Stream<T> distinct() {
		return this.stream.distinct();
	}

	@Override
	public Stream<T> sorted() {
		return this.stream.sorted();
	}

	@Override
	public Stream<T> sorted(Comparator<? super T> comparator) {
		return this.stream.sorted(comparator);
	}

	@Override
	public Stream<T> peek(Consumer<? super T> action) {
		return this.stream.peek(action);
	}

	@Override
	public Stream<T> limit(long maxSize) {
		return this.stream.limit(maxSize);
	}

	@Override
	public Stream<T> skip(long n) {
		return this.stream.skip(n);
	}

	@Override
	public Stream<T> takeWhile(Predicate<? super T> predicate) {
		return this.stream.takeWhile(predicate);
	}

	@Override
	public Stream<T> dropWhile(Predicate<? super T> predicate) {
		return this.stream.dropWhile(predicate);
	}

	@Override
	public boolean isParallel() {
		return this.stream.isParallel();
	}

	@Override
	public Stream<T> sequential() {
		return this.stream.sequential();
	}

	@Override
	public Stream<T> parallel() {
		return this.stream.parallel();
	}

	@Override
	public Stream<T> unordered() {
		return this.stream.unordered();
	}

	@Override
	public Stream<T> onClose(Runnable closeHandler) {
		return this.stream.onClose(closeHandler);
	}

	@Override
	public void close() {
		this.stream.close();
	}
	//#endregion
}