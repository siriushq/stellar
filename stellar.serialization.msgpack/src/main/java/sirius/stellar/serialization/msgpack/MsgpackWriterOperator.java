package sirius.stellar.serialization.msgpack;

import io.avaje.json.JsonIoException;
import org.msgpack.core.MessagePacker;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

sealed interface MsgpackWriterOperator permits MsgpackWriterArrayOperator, MsgpackWriterMapOperator {

	/**
	 * Returns the parent operator for this operator.
	 * This should only not be present if this is the last operation in the chain (base).
	 *
	 * @since 1.0
	 */
	Optional<MsgpackWriterOperator> parent();

	/**
	 * Registers an operation that will produce a nested operator to be operated
	 * upon and written to the stream.
	 *
	 * @param operator Operator that when operated against, will append a new,
	 * more complex element to the stream. This can be nested as many times as
	 * needed for the graph to be created as desired.
	 *
	 * @see MsgpackWriterOperator#operation(MessagePackerConsumer)
	 * @since 1.0
	 */
	default void operation(MsgpackWriterOperator operator) {
		this.operation(operator::operate);
	}

	/**
	 * Registers an operation that produces anything to be written to the stream.
	 *
	 * @param operator A consumer that accepts a {@link MessagePacker} instance to
	 * perform the actual underlying writing that this operation should perform when
	 * it is operated upon.
	 *
	 * @see MsgpackWriterOperator#operation(MsgpackWriterOperator)
	 * @since 1.0
	 */
	void operation(MessagePackerConsumer operator);

	/**
	 * Runs all the registered operations in this operator (operate upon this operator).
	 * <p>
	 * This should only be executed once, though the operations would be
	 * available until this entire operator is out of scope; you can, in
	 * theory, re-use an operator if, e.g., a duplicate value is desired.
	 *
	 * @param packer The packer to run the operation against.
	 * @since 1.0
	 */
	void operate(MessagePacker packer);

	/**
	 * Extension of {@link Consumer} consuming a packer instance but allowing for {@link IOException}
	 * to be automatically caught in the method signature and wrapped in a {@link JsonIoException}.
	 *
	 * @since 1.0
	 */
	interface MessagePackerConsumer extends Consumer<MessagePacker> {

		void consume(MessagePacker packer) throws IOException;

		default void accept(MessagePacker packer) {
			try {
				this.consume(packer);
			} catch (IOException exception) {
				throw new JsonIoException(exception);
			}
		}
	}
}