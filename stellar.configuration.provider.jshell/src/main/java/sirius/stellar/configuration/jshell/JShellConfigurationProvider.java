package sirius.stellar.configuration.jshell;

import io.avaje.spi.ServiceProvider;
import jdk.jshell.JShell;
import jdk.jshell.SnippetEvent;
import jdk.jshell.execution.LocalExecutionControlProvider;
import sirius.stellar.configuration.Configuration;
import sirius.stellar.configuration.FileConfigurationProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.nio.charset.StandardCharsets.*;
import static java.util.UUID.*;
import static java.util.concurrent.TimeUnit.*;
import static java.util.stream.Collectors.*;

/// Implementation of [FileConfigurationProvider] for `.jsh` JShell scripts.
///
/// ### Usage
/// A dismissed expression evaluated in the script must be a `Map` of key-value
/// (`String, String`) entries, which is stored in the scratch variable `$1`.
///
/// You must be running under a JDK (Java Development Kit) to have access to
/// the [JShell] API that this provider uses.
///
/// These scripts are unsandboxed, and must be trusted; remote access to them
/// must be controlled or inaccessible, or they can be used as an attack vector
/// against your system (running under the privileges of your application).
///
/// @see FileConfigurationProvider
/// @see Configuration
@ServiceProvider
public final class JShellConfigurationProvider implements FileConfigurationProvider {

	@Override
	public Map<String, String> get(InputStream stream) throws IOException {
		JShell shell = JShell.builder()
				.out(System.out)
				.err(System.err)
				.executionEngine(new LocalExecutionControlProvider(), null)
				.build();
		Properties registry = System.getProperties();
		String random = randomUUID().toString();

		try (shell; stream) {
			CompletableFuture<Map<String, String>> future = new CompletableFuture<>();
			registry.put(random, future);

			evaluate(shell, "import java.lang.*;");
			evaluate(shell, "import java.util.*;");
			evaluate(shell, "import java.util.concurrent.*;");

			evaluate(shell, new String(stream.readAllBytes(), UTF_8));
			evaluate(shell, "var __$map = $1;");
			evaluate(shell, "var __$registry = System.getProperties();");
			evaluate(shell, "var __$object = __$registry.get(\"" + random + "\");");
			evaluate(shell, "var __$future = (CompletableFuture<Map<String, String>>) __$object;");
			evaluate(shell, "__$future.complete(__$map);");

			return future.get(60, SECONDS);
		} catch (ExecutionException | InterruptedException | TimeoutException exception) {
			throw new IllegalStateException("Failed waiting for Map from JShell script", exception);
		} finally {
			registry.remove(random);
		}
	}

	@Override
	public Set<String> extensions() {
		return Set.of(".jsh", ".jshell");
	}

	/// Evaluate the provided expression, providing error handling and feedback.
	private void evaluate(JShell shell, String expression) {
		List<SnippetEvent> events = shell.eval(expression);
		for (SnippetEvent event : events) {
			if (event.exception() == null) continue;

			String diagnostics = shell.diagnostics(event.snippet())
					.map(diag -> diag.getMessage(Locale.getDefault()))
					.collect(joining("\n"));
			throw new IllegalStateException(diagnostics, event.exception());
		}
	}
}