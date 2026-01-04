package sirius.stellar.facility.stream;

import sirius.stellar.annotation.Contract;
import sirius.stellar.facility.Iterators;
import sirius.stellar.facility.Spliterators;

import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.Path;
import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.*;

import static sirius.stellar.facility.stream.TerminatingDoubleStream.terminalDoubleStream;
import static sirius.stellar.facility.stream.TerminatingIntStream.terminalIntStream;
import static sirius.stellar.facility.stream.TerminatingLongStream.terminalLongStream;

/// Implementation of [Stream] which will automatically close when a terminal operation
/// is executed. While the majority of [Stream]s do not have to be (and should not be)
/// explicitly closed, the following methods provide IO streams (and several third-party
/// libraries may contain similar IO streams) which need to be closed explicitly:
///
///   - [java.nio.file.Files#list(Path)]
///   - [java.nio.file.Files#walk(Path,FileVisitOption...)]
///   - [java.nio.file.Files#walk(Path,int,FileVisitOption...)]
///   - [java.nio.file.Files#find(Path,int,BiPredicate,FileVisitOption...)]
///   - [java.nio.file.Files#lines(Path)]
///   - [java.nio.file.Files#lines(Path,Charset)]
///   - [java.util.Scanner#tokens()]
///   - [java.util.Scanner#findAll(Pattern)]
///   - [java.util.Scanner#findAll(String)]
///
/// There may be more standard methods that have not been mentioned here, as well as
/// new methods introduced in the future. However, these are the most commonly used.
///
/// The motivation for creating such a utility is that the use of try-with-resources
/// in this situation is not the best choice.
///
/// While with try-with-resources, we get the simplest implementation as of now, if
/// we were to close the stream without it, one would not pick try-finally. Instead,
/// one would store the stream, perform the terminal operation, and close it right
/// afterward. It is more pragmatic to close on a terminal operation, as the stream
/// is still entirely useless, however, the IO lock remains.
///
/// With the use of this utility, one is able to completely avoid storing the stream
/// explicitly, as well as avoid creating a new scope with try-with-resources. This
/// can provide a major concision.
///
/// The factory method [#terminalStream(Stream)] is available, designed to be imported
/// statically for a fluent interface. A usage exemplar is as follows:
/// ```
/// List<String> lines = terminalStream(Files.lines(...))
///     .filter(...)
///     .toList(); // This invocation will close the stream.
///
/// terminalStream(Files.list(...))
///     .filter(...)
///     .forEach(path -> ...); // This invocation will close the stream.
/// ```
/// @see TerminatingIntStream
/// @see TerminatingDoubleStream
/// @see TerminatingLongStream
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public class TerminatingStream<T> implements Stream<T> {

	private final Stream<T> stream;

	private TerminatingStream(Stream<T> stream) {
		this.stream = stream;
	}

	/// Creates an auto terminating stream that wraps the provided stream.
	/// This will close the stream when a terminal operation is performed.
	///
	/// @since 1.0
	@Contract("_ -> new")
	public static <T> TerminatingStream<T> terminalStream(Stream<T> stream) {
		return new TerminatingStream<>(stream);
	}

	//#region Terminal Operations
	@Override
	public void forEach(Consumer<? super T> action) {
		try {
			this.stream.forEach(action);
		} finally {
			this.stream.close();
		}
	}

	@Override
	public void forEachOrdered(Consumer<? super T> action) {
		try {
			this.stream.forEachOrdered(action);
		} finally {
			this.stream.close();
		}
	}

	@Override
	public Object[] toArray() {
		try {
			return this.stream.toArray();
		} finally {
			this.stream.close();
		}
	}

	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		try {
			return this.stream.toArray(generator);
		} finally {
			this.stream.close();
		}
	}

	@Override
	public T reduce(T identity, BinaryOperator<T> accumulator) {
		try {
			return this.stream.reduce(identity, accumulator);
		} finally {
			this.stream.close();
		}
	}

	@Override
	public Optional<T> reduce(BinaryOperator<T> accumulator) {
		try {
			return this.stream.reduce(accumulator);
		} finally {
			this.stream.close();
		}
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
		try {
			return this.stream.reduce(identity, accumulator, combiner);
		} finally {
			this.stream.close();
		}
	}

	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
		try {
			return this.stream.collect(supplier, accumulator, combiner);
		} finally {
			this.stream.close();
		}
	}

	@Override
	public <R, A> R collect(Collector<? super T, A, R> collector) {
		try {
			return this.stream.collect(collector);
		} finally {
			this.stream.close();
		}
	}

	@Override
	public List<T> toList() {
		try {
			return this.stream.toList();
		} finally {
			this.stream.close();
		}
	}

	@Override
	public Optional<T> min(Comparator<? super T> comparator) {
		try {
			return this.stream.min(comparator);
		} finally {
			this.stream.close();
		}
	}

	@Override
	public Optional<T> max(Comparator<? super T> comparator) {
		try {
			return this.stream.max(comparator);
		} finally {
			this.stream.close();
		}
	}

	@Override
	public long count() {
		try {
			return this.stream.count();
		} finally {
			this.stream.close();
		}
	}

	@Override
	public boolean anyMatch(Predicate<? super T> predicate) {
		try {
			return this.stream.anyMatch(predicate);
		} finally {
			this.stream.close();
		}
	}

	@Override
	public boolean allMatch(Predicate<? super T> predicate) {
		try {
			return this.stream.allMatch(predicate);
		} finally {
			this.stream.close();
		}
	}

	@Override
	public boolean noneMatch(Predicate<? super T> predicate) {
		try {
			return this.stream.noneMatch(predicate);
		} finally {
			this.stream.close();
		}
	}

	@Override
	public Optional<T> findFirst() {
		try {
			return this.stream.findFirst();
		} finally {
			this.stream.close();
		}
	}

	@Override
	public Optional<T> findAny() {
		try {
			return this.stream.findAny();
		} finally {
			this.stream.close();
		}
	}
	//#endregion

	//#region #iterator & #spliterator
	@Override
	public Iterator<T> iterator() {
		Iterator<T> iterator = this.stream.iterator();
		return Iterators.closing(iterator, this.stream);
	}

	@Override
	public Spliterator<T> spliterator() {
		Spliterator<T> spliterator = this.stream.spliterator();
		return Spliterators.closing(spliterator, this.stream);
	}
	//#endregion

	//#region Intermediate Operations
	@Override
	public Stream<T> filter(Predicate<? super T> predicate) {
		return terminalStream(this.stream.filter(predicate));
	}

	@Override
	public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
		return terminalStream(this.stream.map(mapper));
	}

	@Override
	public IntStream mapToInt(ToIntFunction<? super T> mapper) {
		return terminalIntStream(this.stream.mapToInt(mapper));
	}

	@Override
	public LongStream mapToLong(ToLongFunction<? super T> mapper) {
		return terminalLongStream(this.stream.mapToLong(mapper));
	}

	@Override
	public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
		return terminalDoubleStream(this.stream.mapToDouble(mapper));
	}

	@Override
	public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
		return terminalStream(this.stream.flatMap(mapper));
	}

	@Override
	public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
		return terminalIntStream(this.stream.flatMapToInt(mapper));
	}

	@Override
	public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
		return terminalLongStream(this.stream.flatMapToLong(mapper));
	}

	@Override
	public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
		return terminalDoubleStream(this.stream.flatMapToDouble(mapper));
	}

	@Override
	public <R> Stream<R> mapMulti(BiConsumer<? super T, ? super Consumer<R>> mapper) {
		return terminalStream(this.stream.mapMulti(mapper));
	}

	@Override
	public IntStream mapMultiToInt(BiConsumer<? super T, ? super IntConsumer> mapper) {
		return terminalIntStream(this.stream.mapMultiToInt(mapper));
	}

	@Override
	public LongStream mapMultiToLong(BiConsumer<? super T, ? super LongConsumer> mapper) {
		return terminalLongStream(this.stream.mapMultiToLong(mapper));
	}

	@Override
	public DoubleStream mapMultiToDouble(BiConsumer<? super T, ? super DoubleConsumer> mapper) {
		return terminalDoubleStream(this.stream.mapMultiToDouble(mapper));
	}

	@Override
	public Stream<T> distinct() {
		return terminalStream(this.stream.distinct());
	}

	@Override
	public Stream<T> sorted() {
		return terminalStream(this.stream.sorted());
	}

	@Override
	public Stream<T> sorted(Comparator<? super T> comparator) {
		return terminalStream(this.stream.sorted(comparator));
	}

	@Override
	public Stream<T> peek(Consumer<? super T> action) {
		return terminalStream(this.stream.peek(action));
	}

	@Override
	public Stream<T> limit(long maxSize) {
		return terminalStream(this.stream.limit(maxSize));
	}

	@Override
	public Stream<T> skip(long n) {
		return terminalStream(this.stream.skip(n));
	}

	@Override
	public Stream<T> takeWhile(Predicate<? super T> predicate) {
		return terminalStream(this.stream.takeWhile(predicate));
	}

	@Override
	public Stream<T> dropWhile(Predicate<? super T> predicate) {
		return terminalStream(this.stream.dropWhile(predicate));
	}

	@Override
	public Stream<T> sequential() {
		return terminalStream(this.stream.sequential());
	}

	@Override
	public Stream<T> parallel() {
		return terminalStream(this.stream.parallel());
	}

	@Override
	public Stream<T> unordered() {
		return terminalStream(this.stream.unordered());
	}

	@Override
	public Stream<T> onClose(Runnable closeHandler) {
		return terminalStream(this.stream.onClose(closeHandler));
	}
	//#endregion

	//#region Delegates
	@Override
	public boolean isParallel() {
		return this.stream.isParallel();
	}

	@Override
	public void close() {
		this.stream.close();
	}
	//#endregion
}