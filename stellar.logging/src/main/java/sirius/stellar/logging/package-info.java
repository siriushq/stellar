/// Flexible logging system with a transparent interface and support for
/// many facades and logging backends such as JCL, JUL, SLF4J, etc.
///
/// For most users, the "best default" configuration would be to provide the
/// [sirius.stellar.logging.collect.Collector#console] collector and a severity
/// of [sirius.stellar.logging.LoggerLevel#ERROR]`.severity()`.
///
/// The below example displays common usage patterns:
/// ```
/// import sirius.stellar.logging.Logger;
/// // import static sirius.stellar.logging.Logger.*;
/// import static sirius.stellar.logging.LoggerLevel.*;
///
/// public class Main {
///
///     public static void main(String[] arguments) {
///         Logger.collector(Collector.console());
///         Logger.severity(ERROR.severity());
///
///         //
///
///         // use the statically accessible methods for logging
///         Logger.information("Hello, world!");
///
///         // lazy-evaluate throwable
///         // due to ERROR severity, this throwable is never computed
///         Logger.stacktrace(() -> new Throwable("..."), "Failed to reticulate splines");
///
///         // add dependencies on any dispatchers to use other API, as follows:
///
///         var slf4j = org.slf4j.LoggerFactory.getLogger(Main.class);
///         slf4j.info("Hello from SLF4j!");
///
///         var log4j = org.apache.log4j.Logger.getLogger(Main.class);
///         log4j.info("Hello from Log4J!");
///
///         var log4j2 = org.apache.logging.log4j.LogManager.getLogger(Main.class);
///         log4j2.info("Hello from Log4J2!");
///
///         var jul = java.util.logging.Logger.getLogger(Main.class);
///         jul.info("Hello from java.util.logging!");
///
///         var jcl = org.apache.commons.logging.LogFactory.getLog(Main.class);
///         jcl.info("Hello from Jakarta/Apache Commons Logging!");
///
///         var jboss = org.jboss.logging.Logger.getLogger(Main.class);
///         jboss.info("Hello from JBoss!");
///
///         var jsr379 = System.getLogger(Main.class);
///         jsr379.log(System.Logger.Level.INFO, "Hello from System.Logger!");
///
///         org.tinylog.Logger.info("Hello from tinylog!");
///
///         com.esotericsoftware.minlog.Log.info("Hello from minlog!");
///
///         // add any collectors to your classpath, to have logs sent there
///         // e.g., add SLF4J collector to use SLF4J-based logging backend, like Logback.
///     }
/// }
/// ```
package sirius.stellar.logging;