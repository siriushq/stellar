package sirius.stellar.javadoc;

/// This file solely serves to test parsing of source files, and ability
/// to correctly parse and render Javadoc. This is not a usage example for [Javadoc].
///
/// Features such as `@index`, `@serial...` are not tested here as [Javadoc]
/// does not and will not support them. Most features are supported.
public class JavadocExample {

	//#region @author, @version, @since, @deprecated (and @Deprecated for testing @Documented), @see
	/**
	 * Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
	 * do eiusmod tempor incididunt ut labore et dolore magna aliqua.
	 *
	 * @author John Doe
	 * @author Jane Doe
	 * @version 108247
	 * @since 125987
	 *
	 * @deprecated Use dolor ipsum lorem instead.
	 * @see String Dolor ipsum lorem
	 * @see Object
	 */
	@Deprecated
	public static final class Foo {}
	//#endregion

	//#region @param, @param <T>, @return, @throws, @exception
	/**
	 * Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
	 * do eiusmod tempor incididunt ut labore et dolore magna aliqua.
	 *
	 * @param a the first number
	 * @param b the second number
	 * @throws ArithmeticException big bad math error overflow boom
	 * @return delegate to {@link Math#addExact(int, int)}
	 */
	public static int bar(int a, int b) {
		return Math.addExact(a, b);
	}
	//#endregion

	//#region @summary, @link, @linkplain, @code, @literal, @value, @inheritDoc
	public static final int BAZ = 9000;

	/**
	 * {@summary Don't consider anything...}
	 * Consider {@link JavadocExample} and {@link Object#wait(long, int)}!
	 * Also, maybe take a look at {@linkplain String#chars() this interesting method}...
	 * You could use a {@code ForkJoinPool.commonPool()} for running foo bar baz, if you like.
	 * It would allow you to achieve the status of being {@literal >=9000}!
	 * That is a lot of craziness - that number ({@value BAZ}) is... big!
	 */
	public void baz() {}

	public static final class Baz extends JavadocExample {

		/** @inheritDoc */
		@Override
		public void baz() {}
	}
	//#endregion

	//#region HTML
	/**
	 * Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
	 * do eiusmod tempor incididunt ut labore et dolore magna aliqua.
	 *
	 * <p>
	 * Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
	 * do eiusmod tempor incididunt ut labore et dolore magna aliqua.
	 * </p>
	 *
	 * <pre>{@code
	 * Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
	 * do eiusmod tempor incididunt ut labore et dolore magna aliqua.
	 * }</pre>
	 *
	 * <table>
	 *     <thead>
	 *         <tr>
	 *             <th>Lorem</th>
	 *             <th>Ipsum</th>
	 *         </tr>
	 *     </thead>
	 *     <tbody>
	 *         <tr>
	 *             <td>Dolor</td>
	 *             <td>Sit</td>
	 *         </tr>
	 *         <tr>
	 *             <td>Amet</td>
	 *             <td>Consectetur</td>
	 *         </tr>
	 *     </tbody>
	 * </table>
	 *
	 * <ul>
	 *     <li>Lorem</li>
	 *     <li>Ipsum</li>
	 * </ul>
	 * <ol>
	 *     <li>Lorem</li>
	 *     <li>Ipsum</li>
	 * </ol>
	 */
	public static void boo() {}
	//#endregion

	//#region Precedence
	/**
	 * THIS COMMENT SHOULD NOT BE VISIBLE.
	 * THIS COMMENT SHOULD NOT BE VISIBLE.
	 * THIS COMMENT SHOULD NOT BE VISIBLE.
	 */
	/**
	 * Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
	 * do eiusmod tempor incididunt ut labore et dolore magna aliqua.
	 */
	public static void buz() {}
	//#endregion

	//#region Mixing comments
	// This comment should not affect anything.
	/**
	 * Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
	 * do eiusmod tempor incididunt ut labore et dolore magna aliqua.
	 */
	/* Nor should this one */
	public static void qok() {}
	//#endregion

	//#region Element types
	/** CLASS lorem ipsum dolor sit amet */
	public static final class BarCLASS {}
	/** INTERFACE lorem ipsum dolor sit amet */
	public interface BarINTERFACE {}
	/** METHOD lorem ipsum dolor sit amet */
	public void BarMETHOD() {}
	/** FIELD lorem ipsum dolor sit amet */
	public static final int BarFIELD = 0;
	/** CONSTRUCTOR lorem ipsum dolor sit amet */
	public JavadocExample() {}
	/** ENUM lorem ipsum dolor sit amet */
	public enum BarENUM {}
	/** RECORD lorem ipsum dolor sit amet */
	public record BarRECORD() {}
	//#endregion

	public static final class Markdown {

		//#region @author, @version, @since, @deprecated (and @Deprecated for testing @Documented), @see
		/// Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
		/// do eiusmod tempor incididunt ut labore et dolore magna aliqua.
		///
		/// @author John Doe
		/// @author Jane Doe
		/// @version 108247
		/// @since 125987
		///
		/// @deprecated Use dolor ipsum lorem instead.
		/// @see String Dolor ipsum lorem
		/// @see Object
		@Deprecated
		public static final class Foo {}
		//#endregion

		//#region @param, @param <T>, @return, @throws, @exception
		/// Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
		/// do eiusmod tempor incididunt ut labore et dolore magna aliqua.
		///
		/// @param a the first number
		/// @param b the second number
		/// @throws ArithmeticException big bad math error overflow boom
		/// @return delegate to [#addExact(int,int)]
		public static int bar(int a, int b) {
			return Math.addExact(a, b);
		}
		//#endregion

		//#region @summary, @link, @linkplain, @code, @literal, @value, @inheritDoc
		public static final int BAZ = 9000;

		/// {@summary Don't consider anything...}
		/// Consider [JavadocExample] and [#wait(long,int)]!
		/// Also, maybe take a look at [this interesting method][String#chars()]...
		/// You could use a `ForkJoinPool.commonPool()` for running foo bar baz, if you like.
		/// It would allow you to achieve the status of being {@literal >=9000}!
		/// That is a lot of craziness - that number ({@value BAZ}) is... big!
		public void baz() {}

		public static final class Baz extends JavadocExample {

			/// @inheritDoc
			@Override
			public void baz() {}
		}
		//#endregion

		//#region HTML
		/// Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
		/// do eiusmod tempor incididunt ut labore et dolore magna aliqua.
		///
		/// <p>
		/// Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
		/// do eiusmod tempor incididunt ut labore et dolore magna aliqua.
		/// </p>
		///
		/// <pre>{@code
		/// Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
		/// do eiusmod tempor incididunt ut labore et dolore magna aliqua.
		/// }</pre>
		///
		/// <table>
		///     <thead>
		///         <tr>
		///             <th>Lorem</th>
		///             <th>Ipsum</th>
		///         </tr>
		///     </thead>
		///     <tbody>
		///         <tr>
		///             <td>Dolor</td>
		///             <td>Sit</td>
		///         </tr>
		///         <tr>
		///             <td>Amet</td>
		///             <td>Consectetur</td>
		///         </tr>
		///     </tbody>
		/// </table>
		///
		/// <ul>
		///     <li>Lorem</li>
		///     <li>Ipsum</li>
		/// </ul>
		/// <ol>
		///     <li>Lorem</li>
		///     <li>Ipsum</li>
		/// </ol>
		public static void boo() {}
		//#endregion

		//#region Markdown
		/// Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
		/// do eiusmod tempor incididunt ut labore et dolore magna aliqua.
		///
		/// Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
		/// do eiusmod tempor incididunt ut labore et dolore magna aliqua.
		///
		/// ```
		/// Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
		/// do eiusmod tempor incididunt ut labore et dolore magna aliqua.
		/// ```
		///
		/// | Lorem      | Ipsum       |
		/// |------------|-------------|
		/// | Dolor      | Sit         |
		/// | Amet       | Consectetur |
		///
		/// - Lorem
		/// - Ipsum
		///
		/// * Lorem
		/// * Ipsum
		///
		/// 1. Lorem
		/// 1. Ipsum
		public static void booMarkdown() {}
		//#endregion

		//#region Precedence
		/// THIS COMMENT SHOULD NOT BE VISIBLE.
		/// THIS COMMENT SHOULD NOT BE VISIBLE.
		/// THIS COMMENT SHOULD NOT BE VISIBLE.

		/// Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
		/// do eiusmod tempor incididunt ut labore et dolore magna aliqua.
		public static void buz() {}
		//#endregion

		//#region Mixing comments
		// This comment should not affect anything.
		/// Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
		/// do eiusmod tempor incididunt ut labore et dolore magna aliqua.
		/* Nor should this one */
		public static void qok() {}
		//#endregion

		//#region Element types
		/// CLASS lorem ipsum dolor sit amet
		public static final class BarCLASS {}
		/// INTERFACE lorem ipsum dolor sit amet
		public interface BarINTERFACE {}
		/// METHOD lorem ipsum dolor sit amet
		public void BarMETHOD() {}
		/// FIELD lorem ipsum dolor sit amet
		public static final int BarFIELD = 0;
		/// CONSTRUCTOR lorem ipsum dolor sit amet
		public Markdown() {}
		/// ENUM lorem ipsum dolor sit amet
		public enum BarENUM {}
		/// RECORD lorem ipsum dolor sit amet
		public record BarRECORD() {}
		//#endregion
	}
}