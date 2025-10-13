/// Tiny Java S3 client using avaje-http-client &amp; JDK's built-in XML parsing. \
/// Very small, secure and trivial. Requires Java >11. \
/// Virtual Threads compatible with Java >21.
///
/// ### Goals
/// 1. to perform better than AWS SDK v2.x
/// 2. standalone, be &lt;150kb at runtime (total including dependencies)
/// 3. to implement full support for AWS Signature v4
/// 4. to be smooth to migrate to, coming from AWS SDK v2.x
/// 5. to function correctly on, ideally be tested on:
///     - Amazon S3
///     - MinIO
///     - Garage
///     - Ceph
///     - CloudFlare R2
///     - Backblaze B2
///     - DigitalOcean Spaces
///     - Oracle Object Storage
/// 6. to support essential subset of S3 features:
///     - `CreateBucket`
///     - `HeadBucket`
///     - `ListBuckets`
///     <br/><br/>
///     - `ListObjectsV2`
///     - `GetObject`
///     - `PutObject`
///     - `DeleteObject`
///     - `DeleteObjects`
///     - `HeadObject`
///     - `CopyObject`
///     <br/><br/>
///     - `ListMultipartUploads`
///     - `CreateMultipartUpload`
///     - `CompleteMultipartUpload`
///     - `AbortMultipartUpload`
///     - `UploadPart`
///
/// ### Security &amp; Compatibility
/// 1. The library suppresses sensitive information when logging.
/// 2. XXE hardening is a lower priority, over compatibility &amp; portability.
///    The S3 host is considered a trusted host.
/// 3. Technical "bail out" can be performed simply by using the
///    underlying [io.avaje.http.client.HttpClient].
///
/// @see sirius.stellar.esthree.Esthree
package sirius.stellar.esthree;