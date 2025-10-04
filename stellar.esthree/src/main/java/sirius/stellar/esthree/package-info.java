/**
 * <p>Tiny Java S3 client using avaje-http-client & JDK's built-in XML parsing.
 * <p>Very small, secure and trivial. Requires Java >11.
 * <p>Virtual Threads compatible with Java >21.
 *
 * <h3>Goals</h3>
 * <ol>
 *     <li>to perform better than AWS SDK v2.x</li>
 *     <li>standalone, be <150kb at runtime (total including dependencies)</li>
 *     <li>to implement full support for AWS Signature v4</li>
 *     <li>to be smooth to migrate to, coming from AWS SDK v2.x</li>
 *     <li>to function correctly on, ideally be tested on:<ul>
 *         <li>Amazon S3</li>
 *         <li>MinIO</li>
 *         <li>Garage</li>
 *         <li>Ceph</li>
 *         <li>CloudFlare R2</li>
 *         <li>Backblaze B2</li>
 *         <li>DigitalOcean Spaces</li>
 *         <li>Oracle Object Storage</li>
 *     </ul></li>
 *     <li>to support essential subset of S3 features:<ul>
 *         <li>CreateBucket</li>
 *         <li>HeadBucket</li>
 *         <li>ListBuckets</li>
 *         <br/>
 *         <li>ListObjectsV2</li>
 *         <li>GetObject</li>
 *         <li>PutObject</li>
 *         <li>DeleteObject</li>
 *         <li>DeleteObjects</li>
 *         <li>HeadObject</li>
 *         <li>CopyObject</li>
 *         <br/>
 *         <li>ListMultipartUploads</li>
 *         <li>CreateMultipartUpload</li>
 *         <li>CompleteMultipartUpload</li>
 *         <li>AbortMultipartUpload</li>
 *         <li>UploadPart</li>
 *     </ul></li>
 * </ol>
 *
 * <h3>Security & Compatibility</h3>
 * <ol>
 *     <li>The library suppresses sensitive information when logging.</li>
 *     <li>XXE hardening is a lower priority, over compatibility & portability.
 *     The S3 host is considered a trusted host.</li>
 *     <li>Technical "bail out" can be performed simply by using the
 *     underlying {@link io.avaje.http.client.HttpClient}.</li>
 * </ol>
 *
 * @see sirius.stellar.esthree.Esthree
 */
@NullMarked
package sirius.stellar.esthree;

import org.jspecify.annotations.NullMarked;