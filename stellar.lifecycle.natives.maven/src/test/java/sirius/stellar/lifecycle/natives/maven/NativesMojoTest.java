package sirius.stellar.lifecycle.natives.maven;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoParameter;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

import static java.lang.System.out;

@MojoTest
@DisplayNameGeneration(ReplaceUnderscores.class)
final class NativesMojoTest {

	@Test
	@InjectMojo(
		goal = "natives",
		pom = "src/test/resources/my-project.pom"
	)
	@MojoParameter(
		name = "prefix",
		value = "example-1.0-RC1"
	)
	@MojoParameter(
		name = "output",
		value = "target/test-mojo-output/"
	)
	void natives_goal_downloads_from_HTTPS(NativesMojo mojo)
			throws MojoExecutionException {
		var project = mojo.project();
		var artifact = this.artifact(project);
		project.setArtifact(artifact);

		mojo.execute();
	}

	/// Create a default artifact (usually `jar`) for the provided project.
	/// This is required in order for [NativesMojo] to attach subsequent ones.
	private Artifact artifact(MavenProject project) {
		var group = project.getGroupId();
		var artifact = project.getArtifactId();
		var version = project.getVersion();
		var packaging = project.getPackaging();

		group = "org.example";
		artifact = "example";
		version = "1.0-RC1";
		packaging = "jar";

		var handler = new DefaultArtifactHandler(packaging);
		return new DefaultArtifact(group, artifact, version, null, packaging, null, handler);
	}
}