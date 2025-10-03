package sirius.stellar.platform.generator;

import sirius.stellar.facility.Strings;

import javax.annotation.processing.*;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static javax.lang.model.SourceVersion.*;
import static javax.tools.StandardLocation.*;
import static sirius.stellar.facility.stream.TerminatingStream.*;

/**
 * Build-time annotation processor that generates a {@code META-INF/STELLAR/CONFIGURATION_TEMPLATES.LIST}
 * file containing a list of {@code *.$template} files in the project where the processor is running in,
 * for use in {@link sirius.stellar.platform.Platform} when loading configuration, providing default files.
 * <p>
 * Should any module requires the use of the {@code $template} file extension, and this automatic scanning causes
 * issues, this generator must not be used. {@code META-INF/STELLAR/CONFIGURATION_TEMPLATES.LIST} must be defined
 * manually if template configurations are desired. Extension of every file listed inside is also checked at runtime.
 *
 * @since 1.0
 * @author Mechite
 */
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(RELEASE_8)
public final class PlatformConfigurationProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) return false;

		try {
			Filer filer = this.processingEnv.getFiler();
			FileObject resource = filer.createResource(CLASS_OUTPUT, Strings.EMPTY, "META-INF/STELLAR/CONFIGURATION_TEMPLATES.LIST");

			try (PrintWriter writer = new PrintWriter(resource.openWriter())) {
				Path root = Path.of(Strings.EMPTY).toAbsolutePath();

				for (Path path : terminalStream(Files.walk(root))
						.filter(Files::isRegularFile)
						.filter(path -> path.getFileName().endsWith(".$template"))
						.toList()) {
					String qualified = root.relativize(path).toString().replace("\\", "/");
					writer.println(qualified);
				}
			}
		} catch (IOException exception) {
			this.processingEnv.getMessager().printError("Failed to generate *.$template resource list: " + exception.getMessage());
		}

		return false;
	}
}