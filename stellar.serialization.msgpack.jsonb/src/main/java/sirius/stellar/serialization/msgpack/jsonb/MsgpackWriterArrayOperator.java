package sirius.stellar.serialization.msgpack.jsonb;

import io.avaje.json.JsonIoException;
import sirius.stellar.serialization.msgpack.MessagePacker;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

/// Implementation of [MsgpackWriterOperator] that writes an array.
final class MsgpackWriterArrayOperator implements MsgpackWriterOperator {

	private final MsgpackWriterOperator parent;
	private final Queue<Consumer<MessagePacker>> operators;

	MsgpackWriterArrayOperator() {
		this(null);
	}

	MsgpackWriterArrayOperator(MsgpackWriterOperator parent) {
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
			packer.packArrayHeader(this.operators.size());
			for (Consumer<MessagePacker> operator : this.operators) operator.accept(packer);
		} catch (Exception exception) {
			throw new JsonIoException(new IOException(exception));
		}
	}
}