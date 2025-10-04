/**
 * Adapter implementing support for the MessagePack binary format to avaje-jsonb.
 * <p>
 * If you include this JAR into your project (and with JPMS, the module-path),
 * there are no further steps needed to be taken if you would like to use the
 * default adapter settings; this adapter is located automagically.
 * <p>
 * Currently, the `msgpack-java` module is added as a Git submodule, meaning that
 * you do not need that dependency in your project (and must avoid it) as it is a
 * part of this source tree. See {@code module-info} for clarification.
 * <p>
 * This was done as {@code msgpack-java} lacks JPMS support as of writing.
 *
 * @see <a href="https://github.com/msgpack/msgpack-java/issues/749">msgpack-java#749</a>
 * @see sirius.stellar.serialization.msgpack.MsgpackAdapter
 * @since 1.0
 */
package sirius.stellar.serialization.msgpack;