/// A wrapper of MinIO that can be used as an embedded mock server for
/// testing, or used in a clustered deployment.
///
/// This allows `stellar.logging` to be leveraged as a logging backend for
/// MinIO, and allows for simple cloud-friendly configuration, and may allow
/// for cluster autodiscovery in the future using `stellar.cluster`.
///
/// You must manually build this module on the target OS and hardware in order
/// to get a JAR that is compatible with your system. This module is not published
/// to any repositories (i.e., if used for tests, should be an optional dependency).
package sirius.stellar.esthree.server;