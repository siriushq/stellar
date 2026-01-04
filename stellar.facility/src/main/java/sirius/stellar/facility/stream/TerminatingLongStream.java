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

import static sirius.stellar.facility.stream.TerminatingDoubleStream.terminalDoubleStream;
import static sirius.stellar.facility.stream.TerminatingIntStream.terminalIntStream;
import static sirius.stellar.facility.stream.TerminatingStream.terminalStream;

/// Implementation of [LongStream] which will automatically close
/// when a terminal operation is executed.
///
/// @see TerminatingStream
/// @author Mahied Maruf (mechite)
/// @since 1.0
public class TerminatingLongStream implements LongStream {

    private final LongStream stream;

    private TerminatingLongStream(LongStream stream) {
        this.stream = stream;
    }

    /// Creates an auto terminating stream that wraps the provided stream.
    /// This will close the stream when a terminal operation is performed.
    ///
    /// @since 1.0
    @Contract("_ -> new")
    public static TerminatingLongStream terminalLongStream(LongStream stream) {
        return new TerminatingLongStream(stream);
    }

    //#region Terminal Operations
    @Override
    public void forEach(LongConsumer action) {
        try {
            this.stream.forEach(action);
        } finally {
            this.stream.close();
        }
    }

    @Override
    public void forEachOrdered(LongConsumer action) {
        try {
            this.stream.forEachOrdered(action);
        } finally {
            this.stream.close();
        }
    }

    @Override
    public long[] toArray() {
        try {
            return this.stream.toArray();
        } finally {
            this.stream.close();
        }
    }

	@Override
	public long reduce(long identity, LongBinaryOperator operator) {
        try {
            return this.stream.reduce(identity, operator);
        } finally {
            this.stream.close();
        }
	}

	@Override
	public OptionalLong reduce(LongBinaryOperator operator) {
		try {
            return this.stream.reduce(operator);
        } finally {
            this.stream.close();
        }
	}

	@Override
	public <R> R collect(Supplier<R> supplier, ObjLongConsumer<R> accumulator, BiConsumer<R, R> combiner) {
		try {
            return this.stream.collect(supplier, accumulator, combiner);
        } finally {
            this.stream.close();
        }
	}

	@Override
    public long sum() {
        try {
            return this.stream.sum();
        } finally {
            this.stream.close();
        }
    }

    @Override
    public OptionalLong min() {
        try {
            return this.stream.min();
        } finally {
            this.stream.close();
        }
    }

    @Override
    public OptionalLong max() {
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
    public LongSummaryStatistics summaryStatistics() {
        try {
            return this.stream.summaryStatistics();
        } finally {
            this.stream.close();
        }
    }

    @Override
    public boolean anyMatch(LongPredicate predicate) {
        try {
            return this.stream.anyMatch(predicate);
        } finally {
            this.stream.close();
        }
    }

    @Override
    public boolean allMatch(LongPredicate predicate) {
        try {
            return this.stream.allMatch(predicate);
        } finally {
            this.stream.close();
        }
    }

    @Override
    public boolean noneMatch(LongPredicate predicate) {
        try {
            return this.stream.noneMatch(predicate);
        } finally {
            this.stream.close();
        }
    }

    @Override
    public OptionalLong findFirst() {
        try {
            return this.stream.findFirst();
        } finally {
            this.stream.close();
        }
    }

    @Override
    public OptionalLong findAny() {
        try {
            return this.stream.findAny();
        } finally {
            this.stream.close();
        }
    }

	@Override
	public DoubleStream asDoubleStream() {
		return terminalDoubleStream(this.stream.asDoubleStream());
	}
	//#endregion

	//#region #iterator & #spliterator
	@Override
	public PrimitiveIterator.OfLong iterator() {
		PrimitiveIterator.OfLong iterator = this.stream.iterator();
		this.stream.close();

		Iterator<Long> closing = Iterators.closing(iterator, this.stream);
		return Iterators.primitiveLong(closing);
	}

	@Override
	public Spliterator.OfLong spliterator() {
		Spliterator.OfLong spliterator = this.stream.spliterator();
		this.stream.close();

		Spliterator<Long> closing = Spliterators.closing(spliterator, this.stream);
		return Spliterators.primitiveLong(closing);
	}
	//#endregion

    //#region Intermediate Operations
    @Override
    public LongStream filter(LongPredicate predicate) {
        return terminalLongStream(this.stream.filter(predicate));
    }

    @Override
    public LongStream map(LongUnaryOperator mapper) {
        return terminalLongStream(this.stream.map(mapper));
    }

    @Override
    public <U> Stream<U> mapToObj(LongFunction<? extends U> mapper) {
        return terminalStream(this.stream.mapToObj(mapper));
    }

    @Override
    public IntStream mapToInt(LongToIntFunction mapper) {
        return terminalIntStream(this.stream.mapToInt(mapper));
    }

    @Override
    public DoubleStream mapToDouble(LongToDoubleFunction mapper) {
        return terminalDoubleStream(this.stream.mapToDouble(mapper));
    }

    @Override
    public LongStream flatMap(LongFunction<? extends LongStream> mapper) {
        return terminalLongStream(this.stream.flatMap(mapper));
    }

    @Override
    public LongStream mapMulti(LongMapMultiConsumer mapper) {
        return terminalLongStream(this.stream.mapMulti(mapper));
    }

    @Override
    public LongStream distinct() {
        return terminalLongStream(this.stream.distinct());
    }

    @Override
    public LongStream sorted() {
        return terminalLongStream(this.stream.sorted());
    }

    @Override
    public LongStream peek(LongConsumer action) {
        return terminalLongStream(this.stream.peek(action));
    }

    @Override
    public LongStream limit(long maxSize) {
        return terminalLongStream(this.stream.limit(maxSize));
    }

    @Override
    public LongStream skip(long n) {
        return terminalLongStream(this.stream.skip(n));
    }

    @Override
    public LongStream takeWhile(LongPredicate predicate) {
        return terminalLongStream(this.stream.takeWhile(predicate));
    }

    @Override
    public LongStream dropWhile(LongPredicate predicate) {
        return terminalLongStream(this.stream.dropWhile(predicate));
    }

    @Override
    public Stream<Long> boxed() {
        return terminalStream(this.stream.boxed());
    }
    //#endregion

    //#region Delegates
    @Override
    public LongStream sequential() {
        return terminalLongStream(this.stream.sequential());
    }

    @Override
    public LongStream parallel() {
        return terminalLongStream(this.stream.parallel());
    }

    @Override
    public LongStream unordered() {
        return terminalLongStream(this.stream.unordered());
    }

    @Override
    public LongStream onClose(Runnable closeHandler) {
        return terminalLongStream(this.stream.onClose(closeHandler));
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