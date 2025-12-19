package sirius.stellar.esthree.awssdk;

import io.avaje.http.client.HttpClient;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration.Builder;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.http.SdkHttpResponse;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

import static java.util.concurrent.TimeUnit.*;
import static software.amazon.awssdk.http.Header.*;

/// Interceptor for requests made by AWS SDK v2, to collect metrics.
///
/// The use of the metrics produced by this class requires that the module
/// [io.avaje.http.client] is available on the class-path or module-path.
///
/// @see AwsEsthreeBuilder
final class AwsEsthreeInterceptor
		implements ExecutionInterceptor, Consumer<Builder>, HttpClient.Metrics {

	/// A unique, constant field that is used against [ExecutionAttributes] to
	/// track the precise time it takes for a request to process.
	private static final ExecutionAttribute<Long> TRACKER =
		new ExecutionAttribute<>("sirius.stellar.esthree::metrics");

	private final boolean disabled;

	private final LongAdder totalCount;
	private final LongAdder errorCount;
	private final LongAdder totalMicros;
	private final LongAdder responseBytes;
	private final AtomicLong maxMicros;

	AwsEsthreeInterceptor(boolean disabled) {
		this.disabled = disabled;

		this.totalCount = new LongAdder();
		this.errorCount = new LongAdder();
		this.totalMicros = new LongAdder();
		this.responseBytes = new LongAdder();
		this.maxMicros = new AtomicLong();
	}

	/// Return the current aggregate metrics, then reset the underlying stores.
	AwsEsthreeInterceptor reset() {
		if (this.disabled) return this;

		AwsEsthreeInterceptor clone = new AwsEsthreeInterceptor(false);
		clone.totalCount.add(this.totalCount.sumThenReset());
		clone.errorCount.add(this.errorCount.sumThenReset());
		clone.totalMicros.add(this.totalMicros.sumThenReset());
		clone.responseBytes.add(this.responseBytes.sumThenReset());
		clone.maxMicros.set(this.maxMicros.getAndSet(0));
		return clone;
	}

	@Override
	public void beforeExecution(Context.BeforeExecution context, ExecutionAttributes attributes) {
		if (this.disabled) return;
		attributes.putAttribute(TRACKER, System.nanoTime());
	}

	@Override
	@SuppressWarnings("ConstantValue")
	public void afterExecution(Context.AfterExecution context, ExecutionAttributes attributes) {
		if (this.disabled) return;
		this.totalCount.increment();

		Long start = attributes.getAttribute(TRACKER);
		if (start == null) return;

		long micros = NANOSECONDS.toMicros(System.nanoTime() - start);
		this.totalMicros.add(micros);
		this.maxMicros.accumulateAndGet(micros, Math::max);

		SdkHttpResponse response = context.httpResponse();
		if (response.statusCode() >= 400) this.errorCount.increment();

		response.firstMatchingHeader(CONTENT_LENGTH)
				.map(Long::valueOf)
				.ifPresent(this.responseBytes::add);
	}

	@Override
	public void accept(Builder builder) {
		builder.addExecutionInterceptor(this);
	}

	@Override
	public long totalCount() {
		return this.totalCount.sum();
	}

	@Override
	public long errorCount() {
		return this.errorCount.sum();
	}

	@Override
	public long responseBytes() {
		return this.responseBytes.sum();
	}

	@Override
	public long totalMicros() {
		return this.totalMicros.sum();
	}

	@Override
	public long maxMicros() {
		return this.maxMicros.get();
	}

	@Override
	public long avgMicros() {
		long count = this.totalCount.sum();
		return (count == 0) ? 0 : (this.totalMicros.sum() / count);
	}
}