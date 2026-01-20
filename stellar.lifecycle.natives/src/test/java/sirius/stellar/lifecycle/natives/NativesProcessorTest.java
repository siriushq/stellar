package sirius.stellar.lifecycle.natives;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static com.google.testing.compile.JavaFileObjects.forSourceString;

final class NativesProcessorTest {

	@Test
	void generate() {
		var source = """
		@sirius.stellar.lifecycle.natives.Natives("zlib")
		package org.example.zlib;
		""";

		var location = Natives.class.getProtectionDomain()
				.getCodeSource()
				.getLocation();
		var classpath = new File(location.getFile());

		var compilation = javac()
				.withProcessors(new NativesProcessor())
				.withClasspath(List.of(classpath))
				.compile(forSourceString("org.example.zlib.package-info", source));
		assertThat(compilation)
				.generatedSourceFile("org.example.zlib.ZlibLookup");
	}
}