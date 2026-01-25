/// Flexible logging system with a transparent interface and support for
/// many facades and logging backends such as JCL, JUL, SLF4J, etc.
///
/// For most users, the "best default" configuration would be to add a
/// dependency on the console collector module, and potentially set a
/// severity of [sirius.stellar.logging.LoggerLevel#ERROR]`.severity()`.
///
/// The below example displays common usage patterns:
/// {@snippet lang="java" :
/// import sirius.stellar.logging.Logger;
/// // or import static sirius.stellar.logging.Logger.*;
///
/// import static sirius.stellar.logging.LoggerLevel.*;
///
/// void main()
/// {
/// 	Logger.severity(ERROR.severity());
///
/// 	// Use the statically accessible methods for logging.
/// 	Logger.information("Hello, world!");
///
/// 	// Lazy-evaluate throwables. Due to ERROR severity being set above,
/// 	// both the throwable and message string are never computed.
/// 	Logger.stacktrace(
/// 		() -> new Throwable("..."),
/// 		() -> "Failed to reticulate splines"
/// 	);
///
/// 	// Add dependencies on dispatchers to use other APIs, as follows:
///
/// 	var slf4j = org.slf4j.LoggerFactory
/// 		.getLogger(Main.class);
/// 	slf4j.info("Hello from SLF4j!");
///
/// 	var log4j = org.apache.log4j.Logger
/// 		.getLogger(Main.class);
/// 	log4j.info("Hello from Log4J!");
///
/// 	var log4j2 = org.apache.logging.log4j.LogManager
/// 		.getLogger(Main.class);
/// 	log4j2.info("Hello from Log4J2!");
///
/// 	var jul = java.util.logging.Logger
/// 		.getLogger("org.example.Main");
/// 	jul.info("Hello from java.util.logging!");
///
/// 	var jcl = org.apache.commons.logging.LogFactory
/// 		.getLog(Main.class);
/// 	jcl.info("Hello from Jakarta/Apache Commons Logging!");
///
/// 	var jboss = org.jboss.logging.Logger
/// 		.getLogger(Main.class);
/// 	jboss.info("Hello from JBoss!");
///
/// 	var jsr379 = java.lang.System
/// 		.getLogger("org.example.Main");
/// 	var jsr379_INFO = java.lang.System.
/// 		Logger.Level.INFO;
/// 	jsr379.log(jsr379_INFO, "Hello from System.Logger!");
///
/// 	org.tinylog.Logger
/// 		.info("Hello from tinylog!");
///
/// 	com.esotericsoftware.minlog.Log
/// 		.info("Hello from minlog!");
/// }
/// }
package sirius.stellar.logging;