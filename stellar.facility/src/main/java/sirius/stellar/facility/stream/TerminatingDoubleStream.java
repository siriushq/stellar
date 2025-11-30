package sirius.stellar.facility.stream;

import sirius.stellar.annotation.Contract;
import sirius.stellar.facility.Iterators;
import sirius.stellar.facility.Spliterators;

import java.util.*;
import java.util.function.*;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/// Implementation of [DoubleStream] which will automatically close
/// when a terminal operation is executed.
///
/// @see TerminatingStream
/// @author Mahied Maruf (mechite)
/// @since 1.0
public class TerminatingDoubleStream implements DoubleStream {

    private final DoubleStream stream;

    private TerminatingDoubleStream(DoubleStream stream) {
        this.stream = stream;
    }

    /// Creates an auto terminating stream that wraps the provided stream.
    /// This will close the stream when a terminal operation is performed.
    ///
    /// @since 1.0
    @Contract("_ -> new")
    public static TerminatingDoubleStream terminalDoubleStream(DoubleStream stream) {
        return new TerminatingDoubleStream(stream);
    }

    //#region Terminal Operations
    @Override
    public void forEach(DoubleConsumer action) {
        try {
            this.stream.forEach(action);
        } finally {
            this.stream.close();
        }
    }

    @Override
    public void forEachOrdered(DoubleConsumer action) {
        try {
            this.stream.forEachOrdered(action);
        } finally {
            this.stream.close();
        }
    }

    @Override
    public double[] toArray() {
        try {
            return this.stream.toArray();
        } finally {
            this.stream.close();
        }
    }

	@Override
	public double reduce(double identity, DoubleBinaryOperator operator) {
        try {
            return this.stream.reduce(identity, operator);
        } finally {
            this.stream.close();
        }
	}

	@Override
	public OptionalDouble reduce(DoubleBinaryOperator operator) {
		try {
            return this.stream.reduce(operator);
        } finally {
            this.stream.close();
        }
	}

	@Override
	public <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> accumulator, BiConsumer<R, R> combiner) {
        try {
            return this.stream.collect(supplier, accumulator, combiner);
        } finally {
            this.stream.close();
        }
	}

	@Override
    public double sum() {
        try {
            return this.stream.sum();
        } finally {
            this.stream.close();
        }
    }

    @Override
    public OptionalDouble min() {
        try {
            return this.stream.min();
        } finally {
            this.stream.close();
        }
    }

    @Override
    public OptionalDouble max() {
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
    public DoubleSummaryStatistics summaryStatistics() {
        try {
            return this.stream.summaryStatistics();
        } finally {
            this.stream.close();
        }
    }

    @Override
    public boolean anyMatch(DoublePredicate predicate) {
        try {
            return this.stream.anyMatch(predicate);
        } finally {
            this.stream.close();
        }
    }

    @Override
    public boolean allMatch(DoublePredicate predicate) {
        try {
            return this.stream.allMatch(predicate);
        } finally {
            this.stream.close();
        }
    }

    @Override
    public boolean noneMatch(DoublePredicate predicate) {
        try {
            return this.stream.noneMatch(predicate);
        } finally {
            this.stream.close();
        }
    }

    @Override
    public OptionalDouble findFirst() {
        try {
            return this.stream.findFirst();
        } finally {
            this.stream.close();
        }
    }

    @Override
    public OptionalDouble findAny() {
        try {
            return this.stream.findAny();
        } finally {
            this.stream.close();
        }
    }
    //#endregion

	//#region #iterator & #spliterator
	@Override
	public PrimitiveIterator.OfDouble iterator() {
		PrimitiveIterator.OfDouble iterator = this.stream.iterator();
		this.stream.close();

		Iterator<Double> closing = Iterators.closing(iterator, this.stream);
		return Iterators.primitiveDouble(closing);
	}

	@Override
	public Spliterator.OfDouble spliterator() {
		Spliterator.OfDouble spliterator = this.stream.spliterator();
		this.stream.close();

		Spliterator<Double> closing = Spliterators.closing(spliterator, this.stream);
		return Spliterators.primitiveDouble(closing);
	}
	//#endregion

    //#region Intermediate Operations
    @Override
    public DoubleStream filter(DoublePredicate predicate) {
        return terminalDoubleStream(this.stream.filter(predicate));
    }

    @Override
    public DoubleStream map(DoubleUnaryOperator mapper) {
        return terminalDoubleStream(this.stream.map(mapper));
    }

    @Override
    public <U> Stream<U> mapToObj(DoubleFunction<? extends U> mapper) {
        return TerminatingStream.terminalStream(this.stream.mapToObj(mapper));
    }

    @Override
    public TerminatingIntStream mapToInt(DoubleToIntFunction mapper) {
        return TerminatingIntStream.terminalIntStream(this.stream.mapToInt(mapper));
    }

    @Override
    public TerminatingLongStream mapToLong(DoubleToLongFunction mapper) {
        return TerminatingLongStream.terminalLongStream(this.stream.mapToLong(mapper));
    }

    @Override
    public DoubleStream flatMap(DoubleFunction<? extends DoubleStream> mapper) {
        return terminalDoubleStream(this.stream.flatMap(mapper));
    }

    @Override
    public DoubleStream mapMulti(DoubleMapMultiConsumer mapper) {
        return terminalDoubleStream(this.stream.mapMulti(mapper));
    }

    @Override
    public DoubleStream distinct() {
        return terminalDoubleStream(this.stream.distinct());
    }

    @Override
    public DoubleStream sorted() {
        return terminalDoubleStream(this.stream.sorted());
    }

    @Override
    public DoubleStream peek(DoubleConsumer action) {
        return terminalDoubleStream(this.stream.peek(action));
    }

    @Override
    public DoubleStream limit(long maxSize) {
        return terminalDoubleStream(this.stream.limit(maxSize));
    }

    @Override
    public DoubleStream skip(long n) {
        return terminalDoubleStream(this.stream.skip(n));
    }

    @Override
    public DoubleStream takeWhile(DoublePredicate predicate) {
        return terminalDoubleStream(this.stream.takeWhile(predicate));
    }

    @Override
    public DoubleStream dropWhile(DoublePredicate predicate) {
        return terminalDoubleStream(this.stream.dropWhile(predicate));
    }

    @Override
    public Stream<Double> boxed() {
        return TerminatingStream.terminalStream(this.stream.boxed());
    }
    //#endregion

    //#region Delegates
    @Override
    public DoubleStream sequential() {
        return terminalDoubleStream(this.stream.sequential());
    }

    @Override
    public DoubleStream parallel() {
        return terminalDoubleStream(this.stream.parallel());
    }

    @Override
    public DoubleStream unordered() {
        return terminalDoubleStream(this.stream.unordered());
    }

    @Override
    public DoubleStream onClose(Runnable closeHandler) {
        return terminalDoubleStream(this.stream.onClose(closeHandler));
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