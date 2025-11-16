package sirius.stellar.esthree;

import io.avaje.http.client.HttpException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.URI;

import static java.lang.System.*;
import static org.assertj.core.api.Assertions.*;
import static sirius.stellar.esthree.Esthree.Region.*;

@TestMethodOrder(OrderAnnotation.class)
final class EsthreeTest {

	EsthreeSigner esthreeSigner() {
		return EsthreeSigner.create("minioadmin", "minioadmin", US_EAST_2);
	}

	Esthree esthree() {
		return Esthree.builder()
			.region(US_EAST_2)
			.endpoint("http://127.0.0.1:9000", false)
			.credentials("minioadmin", "minioadmin")
			.build();
	}

	/// Returns `true` if the S3 server is not running, also sending an error message.
	boolean unavailable() {
		try {
			var url = URI.create("https://127.0.0.1:9000").toURL();
			url.openConnection()
					.getInputStream()
					.readAllBytes();
			return false;
		} catch (SSLException exception) {
			return false;
		} catch (IOException exception) {
			err.printf("Failed to connect to mock S3 server, skipping test (%s)\n", exception);
			return true;
		}
	}

	@Test @Order(1)
	@DisplayName("Esthree does not fail during instantiation")
	void notFailsInstantiation() {
		assertThatNoException().isThrownBy(() -> Esthree.builder()
				.credentials("example", "example")
				.build());
	}

	@Test @Order(2)
	@DisplayName("Esthree fails with credential-free instantiation")
	void failsCredentialFreeInstantiation() {
		assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> Esthree.builder().build());
	}

	@Test @Order(3)
	@DisplayName("Esthree successfully creates bucket")
	void successfullyCreatesBucket() {
		if (unavailable()) return;
		assertThatNoException().isThrownBy(() -> {
			var esthree = this.esthree();
			esthree.createBucket("example-123");
		});
	}

	@Test @Order(4)
	@DisplayName("Esthree successfully checks existence of bucket")
	void successfullyChecksBucketExistence() {
		if (unavailable()) return;
		var esthree = this.esthree();
		assertThat(esthree.existsBucket("example-123")).isTrue();
	}

	@Test @Order(5)
	@DisplayName("Esthree successfully deletes bucket")
	void successfullyDeletesBucket() {
		if (unavailable()) return;
		assertThatNoException().isThrownBy(() -> {
			var esthree = this.esthree();
			esthree.deleteBucket("example-123");
		});
	}

	@Test @Order(6)
	@DisplayName("Esthree successfully checks non-existence of bucket")
	void successfullyChecksBucketNonExistence() {
		if (unavailable()) return;
		var esthree = this.esthree();
		assertThat(esthree.existsBucket("example-123")).isFalse();
	}

	@Test @Order(7)
	@DisplayName("Esthree does not expose credentials in stacktrace")
	void doesNotExposeCredentialsStacktrace() {
		if (unavailable()) return;

		var esthree = this.esthree();
		var signer = (DEsthreeSigner) this.esthreeSigner();

		assertThatThrownBy(() -> esthree.deleteBucket("example-123"))
				.isInstanceOf(HttpException.class)
				.extracting(Throwable::getMessage)
				.satisfies(message -> {
					assertThat(message).doesNotContain("minioadmin");
					assertThat(message).doesNotContain(signer.hex(signer.sha256(new byte[0])));
				});
	}
}