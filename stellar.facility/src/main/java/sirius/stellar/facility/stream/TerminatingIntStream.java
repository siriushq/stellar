package sirius.stellar.facility.stream;

import sirius.stellar.annotation.Contract;
import sirius.stellar.facility.Iterators;
import sirius.stellar.facility.Spliterators;

import java.util.*;
import java.util.function.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static sirius.stellar.facility.stream.TerminatingDoubleStream.*;
import static sirius.stellar.facility.stream.TerminatingLongStream.*;
import static sirius.stellar.facility.stream.TerminatingStream.*;

/// Implementation of [IntStream] which will automatically close
/// when a terminal operation is executed.
///
/// @see TerminatingStream
/// @author Mahied Maruf (mechite)
/// @since 1.0
public class TerminatingIntStream implements IntStream {

	private final IntStream stream;

	private TerminatingIntStream(IntStream stream) {
		this.stream = stream;
	}

	/// Creates an auto terminating stream that wraps the provided stream.
	/// This will close the stream when a terminal operation is performed.
	///
	/// @since 1.0
	@Contract("_ -> new")
	public static TerminatingIntStream terminalIntStream(IntStream stream) {
		return new TerminatingIntStream(stream);
	}

	//#region Terminal Operations
    @Override
    public void forEach(IntConsumer action) {
        try {
            this.stream.forEach(action);
        } finally {
            this.stream.close();
        }
    }

    @Override
    public void forEachOrdered(IntConsumer action) {
        try {
            this.stream.forEachOrdered(action);
        } finally {
            this.stream.close();
        }
    }

    @Override
    public int[] toArray() {
        try {
            return this.stream.toArray();
        } finally {
            this.stream.close();
        }
    }

	@Override
	public int reduce(int identity, IntBinaryOperator operator) {
        try {
            return this.stream.reduce(identity, operator);
        } finally {
            this.stream.close();
        }
	}

	@Override
	public OptionalInt reduce(IntBinaryOperator operator) {
        try {
            return this.stream.reduce(operator);
        } finally {
            this.stream.close();
        }
	}

	@Override
	public <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> accumulator, BiConsumer<R, R> combiner) {
        try {
            return this.stream.collect(supplier, accumulator, combiner);
        } finally {
            this.stream.close();
        }
	}

	@Override
    public int sum() {
        try {
            return this.stream.sum();
        } finally {
            this.stream.close();
        }
    }

    @Override
    public OptionalInt min() {
        try {
            return this.stream.min();
        } finally {
            this.stream.close();
        }
    }

    @Override
    public OptionalInt max() {
        try {
            return this.stream.max();
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
    public OptionalDouble average() {
        try {
            return this.stream.average();
        } finally {
            this.stream.close();
        }
    }

    @Override
    public IntSummaryStatistics summaryStatistics() {
        try {
            return this.stream.summaryStatistics();
        } finally {
            this.stream.close();
        }
    }

    @Override
    public boolean anyMatch(IntPredicate predicate) {
        try {
            return this.stream.anyMatch(predicate);
        } finally {
            this.stream.close();
        }
    }

    @Override
    public boolean allMatch(IntPredicate predicate) {
        try {
            return this.stream.allMatch(predicate);
        } finally {
            this.stream.close();
        }
    }

    @Override
    public boolean noneMatch(IntPredicate predicate) {
        try {
            return this.stream.noneMatch(predicate);
        } finally {
            this.stream.close();
        }
    }

    @Override
    public OptionalInt findFirst() {
        try {
            return this.stream.findFirst();
        } finally {
            this.stream.close();
        }
    }

    @Override
    public OptionalInt findAny() {
        try {
            return this.stream.findAny();
        } finally {
            this.stream.close();
        }
    }

	@Override
	public LongStream asLongStream() {
		return terminalLongStream(this.stream.asLongStream());
	}

	@Override
	public DoubleStream asDoubleStream() {
		return terminalDoubleStream(this.stream.asDoubleStream());
	}
	//#endregion

	//#region #iterator & #spliterator
	@Override
	public PrimitiveIterator.OfInt iterator() {
		PrimitiveIterator.OfInt iterator = this.stream.iterator();
		this.stream.close();

		Iterator<Integer> closing = Iterators.closing(iterator, this.stream);
		return Iterators.primitiveInt(closing);
	}

	@Override
	public Spliterator.OfInt spliterator() {
		Spliterator.OfInt spliterator = this.stream.spliterator();
		this.stream.close();

		Spliterator<Integer> closing = Spliterators.closing(spliterator, this.stream);
		return Spliterators.primitiveInt(closing);
	}
	//#endregion

    //#region Intermediate Operations
    @Override
    public IntStream filter(IntPredicate predicate) {
        return terminalIntStream(this.stream.filter(predicate));
    }

    @Override
    public IntStream map(IntUnaryOperator mapper) {
        return terminalIntStream(this.stream.map(mapper));
    }

    @Override
    public <U> Stream<U> mapToObj(IntFunction<? extends U> mapper) {
        return terminalStream(this.stream.mapToObj(mapper));
    }

    @Override
    public LongStream mapToLong(IntToLongFunction mapper) {
        return terminalLongStream(this.stream.mapToLong(mapper));
    }

    @Override
    public DoubleStream mapToDouble(IntToDoubleFunction mapper) {
        return terminalDoubleStream(this.stream.mapToDouble(mapper));
    }

    @Override
    public IntStream flatMap(IntFunction<? extends IntStream> mapper) {
        return terminalIntStream(this.stream.flatMap(mapper));
    }

    @Override
    public IntStream mapMulti(IntMapMultiConsumer mapper) {
        return terminalIntStream(this.stream.mapMulti(mapper));
    }

    @Override
    public IntStream distinct() {
        return terminalIntStream(this.stream.distinct());
    }

    @Override
    public IntStream sorted() {
        return terminalIntStream(this.stream.sorted());
    }

    @Override
    public IntStream peek(IntConsumer action) {
        return terminalIntStream(this.stream.peek(action));
    }

    @Override
    public IntStream limit(long maxSize) {
        return terminalIntStream(this.stream.limit(maxSize));
    }

    @Override
    public IntStream skip(long n) {
        return terminalIntStream(this.stream.skip(n));
    }

    @Override
    public IntStream takeWhile(IntPredicate predicate) {
        return terminalIntStream(this.stream.takeWhile(predicate));
    }

    @Override
    public IntStream dropWhile(IntPredicate predicate) {
        return terminalIntStream(this.stream.dropWhile(predicate));
    }

    @Override
    public Stream<Integer> boxed() {
        return terminalStream(this.stream.boxed());
    }
    //#endregion

    //#region Delegates
    @Override
    public IntStream sequential() {
        return terminalIntStream(this.stream.sequential());
    }

    @Override
    public IntStream parallel() {
        return terminalIntStream(this.stream.parallel());
    }

    @Override
    public IntStream unordered() {
        return terminalIntStream(this.stream.unordered());
    }

    @Override
    public IntStream onClose(Runnable closeHandler) {
        return terminalIntStream(this.stream.onClose(closeHandler));
    }

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