package sirius.stellar.javadoc;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static org.eclipse.jdt.core.JavaCore.*;
import static org.eclipse.jdt.core.dom.AST.*;
import static org.eclipse.jdt.core.dom.ASTParser.*;

public final class JavadocTest123 {

	private static final String source = """

		/// Markdown comment test [String]
		public class Test {

			/**
			 * Adds two numbers.
			 * @param a first
			 * @param b second
			 * @return sum
			 */
			public static int add(int a, int b) { // inline comment
				return a + b;
			}

			/// Subtracts two numbers (see [String] and note `this code`).
			/// Example usage:
			/// ```
			/// test();
			/// ```
			/// Example list:
			/// - Test
			/// - Another test
			///
			/// @param a first
			/// @param a second
			/// @return difference
			public static int subtract(int a, int b) {
				return a - b
			}
		}
	""";

	public static void main(String[] args) throws IOException {
//		String source = Files.readString(Path.of(args[0]));

		ASTParser parser = ASTParser.newParser(getJLSLatest());
		parser.setSource(source.toCharArray());
		parser.setKind(K_COMPILATION_UNIT);

		Map<String, String> options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(latestSupportedJavaVersion(), options);
		parser.setCompilerOptions(options);

		ASTNode node = parser.createAST(null);
		if (!(node instanceof CompilationUnit unit)) throw new IllegalStateException();

		for (Object object : unit.getCommentList()) {
			Comment comment = (Comment) object;
			String text = source.substring(comment.getStartPosition(), comment.getStartPosition() + comment.getLength());
			System.out.println(comment.getClass().getSimpleName() + ": " + text.trim());
		}

		unit.accept(new JavadocVisitor());
	}
}

final class JavadocVisitor extends ASTVisitor {

	@Override
	public boolean visit(MethodDeclaration node) {
		var javadoc = node.getJavadoc();
		if (javadoc == null) return true;

		System.out.println("\nJavaDoc for method: " + node.getName());
		for (Object tag : javadoc.tags()) {
			TagElement element = (TagElement) tag;
			String content = ((Stream<String>) element.fragments()
					.stream()
					.map(Object::toString))
					.collect(joining("\n"));
			System.out.println(element.getTagName() + " -> " + content);
		}
		return true;
	}
}
