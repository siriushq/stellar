/// A wrapper of MinIO that can be used as an embedded mock server for tests.
///
/// This allows `stellar.logging` to be leveraged as a logging backend for MinIO,
/// and for configuration through a builder-style pattern.
package sirius.stellar.esthree.mock;