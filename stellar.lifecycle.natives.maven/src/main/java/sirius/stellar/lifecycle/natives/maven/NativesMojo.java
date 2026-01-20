package sirius.stellar.lifecycle.natives.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import javax.inject.Inject;
import javax.inject.Named;
import java.nio.file.Path;
import java.util.Set;

import static java.lang.System.out;
import static java.text.MessageFormat.format;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PACKAGE;

@Mojo(name = "natives", defaultPhase = PACKAGE)
public final class NativesMojo extends AbstractMojo {

	@Parameter
	private Set<NativesBinary> binaries;

	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject project;

	@Inject @Named("default")
	private MavenProjectHelper helper;

	@Parameter(defaultValue = "target/")
	private Path output;

	@Parameter(defaultValue = "${project.build.finalName}", readonly = true)
	private String prefix;

	@Parameter(defaultValue = "false", property = "skipNatives")
	private boolean skip;

	/// Obtain the currently loaded [#project].
	MavenProject project() {
		return this.project;
	}

	@Override
	public void execute() throws MojoExecutionException {
		if (this.skip) {
			getLog().warn("Skipping native classifier artifacts generation (-DskipNatives)");
			return;
		}

		try {
			getLog().info("Found " + this.binaries.size() + " binary entries");
			for (NativesBinary binary : this.binaries) {
				String name = binary.name(), classifier = binary.classifier();
				getLog().info(format("Processing binary \"{0}\" for classifier \"{1}\"", name, classifier));
				getLog().debug(binary.toString());

				binary.create(this.output, this.prefix);
				if (!binary.inserted()) binary.insert();

				this.helper.attachArtifact(this.project, "jar", classifier, binary.path().toFile());
			}
		} finally {
			for (NativesBinary binary : this.binaries) {
				String name = binary.name(), classifier = binary.classifier();
				getLog().info(format("Processed binary \"{0}\" for classifier \"{1}\"", name, classifier));
				binary.close();
			}
		}
	}
}