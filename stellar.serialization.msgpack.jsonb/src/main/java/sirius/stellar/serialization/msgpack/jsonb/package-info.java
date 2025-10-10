/// Adapter implementing support for the MessagePack binary format to avaje-jsonb.
///
/// If you include this JAR into your project (and with JPMS, the module-path),
/// there are no further steps needed to be taken if you would like to use the
/// default adapter settings; this adapter is located automagically.
///
/// Currently, the `msgpack-java` module is added as a Git submodule, meaning that
/// you do not need that dependency in your project (and must avoid it) as it is a
/// part of this source tree. See `module-info` for clarification.
///
/// This was done as `msgpack-java` lacks JPMS support as of writing.
///
/// @see [msgpack-java#749](https://github.com/msgpack/msgpack-java/issues/749)
/// @see sirius.stellar.serialization.msgpack.jsonb.MsgpackAdapter
/// @since 1.0
package sirius.stellar.serialization.msgpack.jsonb;