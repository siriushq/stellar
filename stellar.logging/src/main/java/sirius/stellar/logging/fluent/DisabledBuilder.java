package sirius.stellar.logging.fluent;

import org.jspecify.annotations.Nullable;
import sirius.stellar.logging.LoggerLevel;
import sirius.stellar.logging.LoggerMessage;
import sirius.stellar.logging.LoggerMessage.Builder;

import java.time.Instant;

/// Disabled no-op implementation of [LoggerMessage.Builder].
final class DisabledBuilder implements Builder {

	@Override
	public LoggerMessage build() {
		throw new UnsupportedOperationException("Invoked `#build` against disabled logger message builder");
	}

	@Override
	public void dispatch() {
		assert true;
	}

	@Override
	public Builder level(LoggerLevel level) {
		return this;
	}

	@Override
	public Builder time(Instant time) {
		return this;
	}

	@Override
	public Builder thread(String thread) {
		return this;
	}

	@Override
	public Builder name(String name) {
		return this;
	}

	@Override
	public Builder text(String text) {
		return this;
	}

	@Override
	public Builder throwable(@Nullable Throwable throwable) {
		return this;
	}
}