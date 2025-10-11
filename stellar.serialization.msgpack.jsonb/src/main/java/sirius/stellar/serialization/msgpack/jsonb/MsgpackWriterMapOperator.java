package sirius.stellar.serialization.msgpack.jsonb;

import io.avaje.json.JsonIoException;
import org.jspecify.annotations.Nullable;
import sirius.stellar.serialization.msgpack.MessagePacker;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;

/// Implementation of [MsgpackWriterOperator] that writes a map.
final class MsgpackWriterMapOperator implements MsgpackWriterOperator {

	private final @Nullable MsgpackWriterOperator parent;
	private final Queue<Consumer<MessagePacker>> operators;

	MsgpackWriterMapOperator() {
		this(null);
	}

	MsgpackWriterMapOperator(@Nullable MsgpackWriterOperator parent) {
		this.parent = parent;
		this.operators = new LinkedList<>();
	}

	@Override
	public Optional<MsgpackWriterOperator> parent() {
		return Optional.ofNullable(this.parent);
	}

	@Override
	public void operation(MessagePackerConsumer operator) {
		this.operators.add(operator);
	}

	@Override
	public void operate(MessagePacker packer) {
		try {
			packer.packMapHeader(this.operators.size() / 2);
			for (Consumer<MessagePacker> operator : this.operators) operator.accept(packer);
		} catch (IOException exception) {
			throw new JsonIoException(new IOException(exception));
		}
	}
}