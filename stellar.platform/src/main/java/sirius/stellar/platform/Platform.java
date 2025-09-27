package sirius.stellar.platform;

import io.avaje.inject.BeanScope;
import io.avaje.jsonb.Json;
import org.jetbrains.annotations.Contract;
import sirius.stellar.facility.concurrent.Latched;
import sirius.stellar.facility.doctation.Internal;
import sirius.stellar.facility.doctation.Nullable;
import sirius.stellar.logging.Logger;
import sirius.stellar.logging.collect.Collector;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.lang.management.*;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

import static java.lang.management.ManagementFactory.*;
import static sirius.stellar.platform.PlatformConfiguration.*;

/**
 * This class represents the central component of the framework facilitating
 * inversion-of-control and the management of dependencies within applications.
 * <p>
 * It provides an entrypoint that can be used by the application to achieve a
 * very opinionated startup process where the only classes in any given source
 * tree will be data models, singleton beans, controllers, views, etc.
 * <p>
 * Injecting this class into any given bean provides a lot of global metadata
 * about the current process. The static methods provided by this class are
 * only to be used to retro-fit applications written contrary to the paradigm
 * promoted by the use of this class, and their use should be avoided.
 * <p>
 * Here is a full list of provided beans in the inversion-of-control container:
 * <ul>
 *     <li>
 *         {@link Platform} - An instance of this class which can be used to
 *         retrieve any global metadata about the current process, for example,
 *         the startup time using {@link Platform#startup()}.
 *     </li>
 *     <li>
 *         {@link Runtime} - Retrieved with {@link Runtime#getRuntime()}.
 *     </li>
 *     <li>
 *         {@link ExecutorService} - A virtual thread executor service that is
 *         created using {@link Executors#newVirtualThreadPerTaskExecutor()}.
 *     </li>
 *     <li>
 *         {@link RandomGenerator} - {@link SecureRandom#SecureRandom()} is used
 *         to create the provided instance (prioritizing randomness) - if this is
 *         undesirable, a different instance could be provided in your own factory,
 *         then that retrieved directly, instead of this abstract interface, in
 *         order to use it.
 *     </li>
 *     <li>
 *         Useful JMX beans, including:
 *         <ul>
 *     	       <li>{@link ClassLoadingMXBean}</li>
 *             <li>{@link CompilationMXBean}</li>
 *             <li>
 *                 {@link GarbageCollectorMXBean}s
 *                 (inject using a {@link List} or {@link Set})
 *             </li>
 *             <li>{@link MemoryMXBean}</li>
 *             <li>
 *                 {@link MemoryManagerMXBean}s
 *                 (inject using a {@link List} or {@link Set})
 *             </li>
 *             <li>
 *                 {@link MemoryPoolMXBean}s
 *                 (inject using a {@link List} or {@link Set})
 *             </li>
 *             <li>{@link OperatingSystemMXBean}</li>
 *             <li>{@link RuntimeMXBean}</li>
 *             <li>{@link ThreadMXBean}</li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * @since 1u1
 * @author Mechite
 */
@Json
public final class Platform implements AutoCloseable, Serializable {

	private static final Latched<BeanScope> scope = new Latched<>();

	@Serial
	private static final long serialVersionUID = 561030072926744922L;

	@Json.Ignore
	private transient final String[] arguments;

	@Json.Ignore
	private transient final List<Consumer<BeanScope>> hooks;

	@Json.Property("uid")
	private final String identifier;

	@Json.Property("stp")
	private final Instant startup;

	@Json.Property("cfg")
	private final Properties configuration;

	@Json.Property("ins")
	private final Set<Platform> instances;

	/**
	 * Constructor used for deserialization of externally loaded {@link Platform} instances.
	 * This should never be called in a written application source directly.
	 * <p>
	 * This constructor takes every {@link Json.Property} annotated field as an argument,
	 * however, does not pass through all the values directly to the fields, for obvious
	 * reasons.
	 * <p>
	 * It also sets {@code null} values for every transient / {@link Json.Ignore}d field.
	 */
	@Internal
	@Json.Creator
	Platform(String identifier, Instant startup, Properties configuration, Set<Platform> instances) {
		this.arguments = null;
		this.hooks = null;

		this.identifier = identifier;
		this.startup = startup;
		this.configuration = configuration;
		this.instances = instances.stream()
				.filter(instance -> !instance.equals(Platform.platform()))
				.collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * Constructor used to instantiate the bean to insert it into the scope.
	 * This should not need to be called in a written application source directly, unless
	 * one is not using {@link Platform#main(String...)} to bootstrap their application.
	 * <p>
	 * This constructor will also populate the configuration object by loading potential
	 * file candidates or default resource versions, as well as environment variables and
	 * application properties, i.e. (in order - any source that loads files with wildcard
	 * as demonstrated in the name shown first selects the files in lexicographical order,
	 * and from the current working directory):
	 * <ol>
	 *     <li>*.properties and *.env (both parsed with {@link Properties} as properties,
	 *     which is a lenient parser, as properties has some features that env lacks such
	 *     as colon rather than equals as key value separator, or exclamation points for
	 *     comments - good practice would be to stick to what dotenv supports, or rename
	 *     the file for future proofing)</li>
	 *     <li>{@link System#getProperties() System Properties}</li>
	 *     <li>{@link System#getenv() Environment Variables}</li>
	 * </ol>
	 * Everything listed here can have a default version defined by appending {@code .$template}
	 * to the end of the extension, e.g., {@code .properties.$template} and putting it anywhere
	 * in any JAR on the module path, or anywhere on the classpath, and ensuring that the
	 * generator is being used during compilation. It will be copied into the current working
	 * directory with the extension stripped unless a file already exists there (done before
	 * anything is loaded). The file names for each template must be kept unique.
	 * <p>
	 * Default configurations are useful to add comments to document the options inside them
	 * as well as to provide a file, with default options set so that the application will
	 * be able to function without any configuration.
	 * <p>
	 * However, it should be noted that environment variables should always be preferred
	 * over any other option, and will override any other option as they are loaded last.
	 */
	public Platform(@Nullable String[] arguments, Instant startup) throws IOException {
		this.arguments = arguments != null ? arguments : new String[0];
		this.hooks = new ArrayList<>();

		this.identifier = Instant.now().toEpochMilli() + "-" + UUID.randomUUID();
		this.startup = startup;

		this.configuration = loadConfiguration();
		this.instances = new HashSet<>();
	}

	/**
	 * The entrypoint that should be used if the framework is being used in full.
	 * <p>
	 * This will automatically create an inversion-of-control container and wire
	 * all dependencies using the {@link java.util.ServiceLoader}.
	 * <p>
	 * When this entrypoint is being used, the {@link Platform} class is available
	 * inside the container and can be injected into any wired beans. For example:
	 * <pre>{@code
	 *     @Component
	 *     class Example {
	 *
	 *         @Inject
	 *         Example(Platform platform) {
	 *             // ...
	 *         }
	 *     }
	 * }</pre>
	 * However, at the point in time in which the constructor would be invoked, only
	 * the instance methods against this class should be invoked (i.e., against the
	 * instance that is injected). Methods such as {@link Platform#scope()} should
	 * not be called as the scope has not finished being built. Should these methods
	 * be needed, they can be accessed in an instance method inside a given class
	 * annotated with {@link io.avaje.inject.PostConstruct}, as follows:
	 * <pre>{@code
	 *     // (omit class declaration, @Component, etc for brevity) ...
	 *     @PostConstruct
	 *     public void postConstruct(BeanScope scope) {
	 *         // ...
	 *     }
	 * }</pre>
	 * The {@link BeanScope} argument is provided here to demonstrate how the scope
	 * can be injected directly through the method as this is a common use case (though,
	 * an anti-pattern in most cases). However, it can be omitted, and any method
	 * simply annotated with {@link io.avaje.inject.PostConstruct} will be executed.
	 * <p>
	 * This also calls {@code Logger.collector(Collector.console())} to forward logging
	 * to console, as if this entrypoint is being used, that is most likely desirable
	 * behavior. If this is not desirable behavior, this entrypoint should not be used
	 * in favor of a domain-specific fix, eventually migrating to use the framework in
	 * full and allowing the logging system to collect logs to console as well.
	 * <p>
	 * Remaining on the topic of potential side effects, this method allows for a small
	 * amount of flexibility in the contract for the "arguments" parameter; not only is
	 * it using a declaration with varargs, but it also can be provided as null (which
	 * case it will simply be replaced with {@code new String[0]}). It should not
	 * contain null values within the array, however.
	 * <p>
	 * This functionality should not provide any benefit to applications conforming to
	 * well-executed paradigms, but is beneficial to retrofit applications that may have
	 * complex launch sequences that may call this method from a different context to the
	 * common Java Virtual Machine initialization; in any case, the contract here is very
	 * lenient and allows for any of these environments to be retrofitted, stating this as
	 * the application entrypoint.
	 *
	 * @see Platform#Platform(String[], Instant)
	 * @since 1u1
	 */
	public static void main(String... arguments) throws IOException {
		Instant startup = Instant.ofEpochMilli(getRuntimeMXBean().getStartTime());
		Logger.collector(Collector.console());

		scope.set(BeanScope.builder()
				.bean(Platform.class, new Platform(arguments, startup))
				.shutdownHook(true)
				.build());
		scope.release();

		Runtime.getRuntime().addShutdownHook(new Thread(Logger::close));
	}

	/**
	 * Returns the command line arguments associated with the current process, if
	 * any, or simply returns an empty array.
	 *
	 * @since 1u1
	 */
	public String[] arguments() {
		return arguments;
	}

	/**
	 * Returns the unique identifier of this platform bean.
	 * <p>
	 * Essentially, this allows you to uniquely identify and address a given node
	 * within a large cluster. The identifier that is used is almost guaranteed to
	 * be unique, as it is a timestamp prefixed {@link UUID}.
	 *
	 * @since 1u1
	 */
	public String identifier() {
		return this.identifier;
	}

	/**
	 * Returns when this platform bean was instantiated.
	 * <p>
	 * Essentially, this method allows you to return the time in which the process
	 * was started, so that {@link RuntimeMXBean#getStartTime()} does not have to
	 * be used, relieving the direct dependency on {@code java.lang.management}.
	 *
	 * @since 1u1
	 */
	public Instant startup() {
		return this.startup;
	}

	/**
	 * Returns a {@link Properties} object that represents the configuration that
	 * is held by this platform bean. This method is not computationally expensive.
	 *
	 * @since 1u1
	 */
	public Properties configuration() {
		return this.configuration;
	}

	/**
	 * Returns a set for all the {@link Platform} instances that the current one has
	 * record of, which should all be alive. Instances are eventually removed from the
	 * underlying set whenever they are not returning their heartbeat.
	 *
	 * @since 1u1
	 */
	public Set<Platform> instances() {
		return this.instances;
	}

	/**
	 * Returns the duration in which this platform bean has been available.
	 * <p>
	 * Essentially, this method allows you to return the total time in which the process
	 * has been alive for so that {@link RuntimeMXBean#getUptime()} does not have to be
	 * used, relieving the direct dependency on {@code java.lang.management}.
	 *
	 * @since 1u1
	 */
	@Json.Property("upt")
	public Duration uptime() {
		return Duration.ofMillis(scope().get(RuntimeMXBean.class).getUptime());
	}

	/**
	 * Returns the duration between when this platform bean has been available and the
	 * provided {@link Temporal} (whose precision influences the output precision).
	 * <p>
	 * Essentially, this method allows you to return the total time in which the process
	 * has been alive for so that {@code RuntimeMXBean.getUptime()} does not have to be
	 * used, relieving the direct dependency on {@code java.lang.management}.
	 * <p>
	 * This method is different to {@link Platform#uptime()} as you could provide the
	 * return value of {@link Platform#startup()} to get an uptime value that does not
	 * use {@link RuntimeMXBean} at the invocation level for calculation, while that
	 * method retrieves the {@link RuntimeMXBean} and invokes {@code getUptime()}.
	 *
	 * @since 1u1
	 */
	@Contract("_ -> new")
	public Duration uptime(Temporal temporal) {
		return Duration.between(temporal, this.startup);
	}

	/**
	 * Registers a shutdown hook which executes at shutdown and consumes {@link BeanScope}
	 * to allow for dependencies to be accessed inside the scope of the hook.
	 * <p>
	 * Essentially, this method allows you to safely add shutdown hooks that will not cause
	 * logging to be negatively affected by a hook registered late, as it will run alongside
	 * the {@link AutoCloseable#close()} method for any auto-closeable beans that are
	 * registered inside the scope. A usage exemplar is as follows:
	 * <pre>{@code
	 *     platform.shutdownHook(scope -> {
	 *         scope.get(Runtime.class).exec("echo Hello, world!");
	 *     });
	 * }</pre>
	 *
	 * @since 1u1
	 */
	@Contract(value = "_ -> this")
	public Platform shutdownHook(Consumer<BeanScope> hook) {
		if (this.hooks == null) throw new IllegalStateException("shutdownHook(Consumer) cannot be run against deserialized Platform instance");

		this.hooks.add(hook);
		return this;
	}

	@Override
	public void close() {
		for (Consumer<BeanScope> hook : this.hooks) hook.accept(Platform.scope());
	}

	@Override
	public boolean equals(Object object) {
		return (object == this) || (object instanceof Platform platform) && (this.identifier.equalsIgnoreCase(platform.identifier));
	}

	/**
	 * Returns the {@link BeanScope} associated with the current process.
	 * <p>
	 * If all beans have not yet been wired, this method will wait until the scope
	 * has finished being created (and all beans being wired).
	 * <p>
	 * Therefore, this method is entirely thread-safe, though it should not be called
	 * during the creation/instantiation of a bean as this would cause a deadlock and
	 * prevent the scope from ever finishing being created.
	 * <p>
	 * It is also quite common that many code analysis tools will report the use of
	 * an {@link AutoCloseable} without ever calling the {@link AutoCloseable#close()}
	 * method or using a "try-with-resources" statement - the scope should never be
	 * closed through this method unless required, as a shutdown hook is always already
	 * registered for it.
	 *
	 * @since 1u1
	 */
	public static BeanScope scope() {
		return scope.get();
	}

	/**
	 * Returns the instance of {@link Platform} associated with the current process.
	 * <p>
	 * This is shorthand to directly invoking {@code Platform.scope().get(Platform.class)}
	 * and is therefore thread-safe and will block the current thread until the scope is
	 * available (more information available in the {@link Platform#scope()} method).
	 *
	 * @see Platform#scope()
	 * @since 1u1
	 */
	public static Platform platform() {
		return scope().get(Platform.class);
	}
}