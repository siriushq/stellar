package sirius.stellar.facility;

import org.jetbrains.annotations.Contract;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Provides a facility for modifying and examining {@link Throwable}s.
 * This class is entirely {@code null} safe and no operations should cause a {@link NullPointerException}.
 *
 * @since 1.0
 * @author Mechite
 */
public final class Throwables {

	/**
	 * Performs an action for each cause of the provided throwable.
	 * <p>
	 * This method can handle a recursive cause structure that would otherwise cause an infinite
	 * loop if iterated this way, as it will not allow for an exception to be consumed twice.
	 *
	 * @see Throwables#causes(Throwable)
	 * @see Throwables#stream(Throwable)
	 * @since 1.0
	 */
	public static void forEach(Throwable throwable, Consumer<Throwable> consumer) {
		if (throwable == null || consumer == null) return;
		stream(throwable).forEach(consumer);
	}

	/**
	 * Gets a list of all the causes in the exception chain for the provided throwable.
	 * <p>
	 * This method can handle a recursive cause structure that would otherwise cause an infinite
	 * loop if iterated this way, as it will not allow for an exception to be consumed twice.
	 *
	 * @return An unmodifiable list of causes, or empty if the provided throwable is null.
	 * @see Throwables#forEach(Throwable, Consumer)
	 * @see Throwables#stream(Throwable)
	 * @since 1.0
	 */
	@Contract(value = "_ -> new", pure = true)
	public static List<Throwable> causes(Throwable throwable) {
		if (throwable == null) return Collections.emptyList();
		return stream(throwable).toList();
	}

	/**
	 * Streams all the causes of the provided throwable.
	 * <p>
	 * This method can handle a recursive cause structure that would otherwise cause an infinite
	 * loop if iterated this way, as it will not allow for an exception to be consumed twice.
	 *
	 * @return A stream for the causes, or empty if the provided throwable is null.
	 * @see Throwables#forEach(Throwable, Consumer)
	 * @see Throwables#causes(Throwable)
	 * @since 1.0
 	 */
	@Contract(value = "_ -> new", pure = true)
	public static Stream<Throwable> stream(Throwable throwable) {
		if (throwable == null) return Stream.empty();

		Set<Throwable> processed = new HashSet<>();
        processed.add(throwable);

        return Stream.concat(Stream.of(throwable), Stream.iterate(throwable.getCause(), cause -> cause != null && !processed.contains(cause), Throwable::getCause));
	}

	/**
	 * Returns a stacktrace string for the provided throwable.
	 * The string is composed of {@link Throwable#toString()} and then data previously recorded
	 * by {@link Throwable#fillInStackTrace()}. The format of this information depends on the
	 * implementation, but the following example may be regarded as typical:
	 *
	 * <pre>
	 * HighLevelException: MidLevelException: LowLevelException
	 *     at Junk.a(Junk.java:13)
	 *     at Junk.main(Junk.java:4)
	 * Caused by: MidLevelException: LowLevelException
	 *     at Junk.c(Junk.java:23)
	 *     at Junk.b(Junk.java:17)
	 *     at Junk.a(Junk.java:11)
	 *     ... 1 more
	 * Caused by: LowLevelException
	 *     at Junk.e(Junk.java:30)
	 *     at Junk.d(Junk.java:27)
	 *     at Junk.c(Junk.java:21)
	 *     ... 3 more
	 * </pre>
	 *
	 * @return The stacktrace or the string {@code "null"} if the provided throwable is null.
	 * @see Throwable#printStackTrace() Read the Throwable#printStackTrace() method for insight.
	 * @since 1.0
	 */
	@Contract(value = "_ -> new", pure = true)
	public static String stacktrace(Throwable throwable) {
		if (throwable == null) return "null";
		StringWriter writer = new StringWriter();
		try (PrintWriter printWriter = new PrintWriter(writer)) {
			throwable.printStackTrace(printWriter);
			return writer.toString();
		}
	}
}