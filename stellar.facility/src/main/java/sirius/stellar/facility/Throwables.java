package sirius.stellar.facility;

import org.jspecify.annotations.Nullable;
import sirius.stellar.annotation.Contract;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/// Provides a facility for modifying and examining [Throwable]s.
/// This class is entirely `null` safe and no operations should cause a [NullPointerException].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class Throwables {

	/// Performs an action for each cause of the provided throwable.
	///
	/// This method can handle a recursive cause structure that would otherwise cause an infinite
	/// loop if iterated this way, as it will not allow for an exception to be consumed twice.
	///
	/// @see Throwables#causes(Throwable)
	/// @see Throwables#stream(Throwable)
	/// @since 1.0
	public static void forEach(@Nullable Throwable throwable, @Nullable Consumer<Throwable> consumer) {
		if (throwable == null || consumer == null) return;
		stream(throwable).forEach(consumer);
	}

	/// Gets a list of all the causes in the exception chain for the provided throwable.
	///
	/// This method can handle a recursive cause structure that would otherwise cause an infinite
	/// loop if iterated this way, as it will not allow for an exception to be consumed twice.
	///
	/// @return An unmodifiable list of causes, or empty if the provided throwable is null.
	/// @see Throwables#forEach(Throwable, Consumer)
	/// @see Throwables#stream(Throwable)
	/// @since 1.0
	@Contract("_ -> new")
	public static List<Throwable> causes(@Nullable Throwable throwable) {
		if (throwable == null) return Collections.emptyList();
		return stream(throwable).toList();
	}

	/// Streams all the causes of the provided throwable.
	///
	/// This method can handle a recursive cause structure that would otherwise cause an infinite
	/// loop if iterated this way, as it will not allow for an exception to be consumed twice.
	///
	/// @return A stream for the causes, or empty if the provided throwable is null.
	/// @see Throwables#forEach(Throwable, Consumer)
	/// @see Throwables#causes(Throwable)
	/// @since 1.0
	@Contract("_ -> new")
	public static Stream<Throwable> stream(@Nullable Throwable throwable) {
		if (throwable == null) return Stream.empty();

		List<Throwable> causes = new ArrayList<>();
		Set<Throwable> processed = new HashSet<>();
		Throwable current = throwable;

		while (current != null && processed.add(current)) {
			causes.add(current);
			current = current.getCause();
		}
		return causes.stream();
	}
}