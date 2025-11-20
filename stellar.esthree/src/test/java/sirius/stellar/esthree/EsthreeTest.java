package sirius.stellar.esthree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.time.Instant;

import static java.lang.System.*;
import static java.time.temporal.ChronoUnit.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.*;
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
	void instantiation() {
		assertThatNoException().isThrownBy(() -> Esthree.builder()
				.credentials("example", "example")
				.build());
	}

	@Test @Order(2)
	@DisplayName("Esthree fails with credential-free instantiation")
	void credentialFreeInstantiation() {
		assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> Esthree.builder().build());
	}

	@Test @Order(3)
	@DisplayName("Esthree successfully creates bucket")
	void createBucket() {
		if (unavailable()) return;
		assertThatNoException().isThrownBy(() -> {
			var esthree = this.esthree();
			esthree.createBucket("example-123");
		});
	}

	@Test @Order(4)
	@DisplayName("Esthree successfully checks existence of bucket")
	void checkBucketExistence() {
		if (unavailable()) return;
		var esthree = this.esthree();
		assertThat(esthree.existsBucket("example-123")).isTrue();
	}

	@Test @Order(5)
	@DisplayName("Esthree successfully deletes bucket")
	void deleteBucket() {
		if (unavailable()) return;
		assertThatNoException().isThrownBy(() -> {
			var esthree = this.esthree();
			esthree.deleteBucket("example-123");
		});
	}

	@Test @Order(6)
	@DisplayName("Esthree successfully checks non-existence of bucket")
	void checksBucketNonExistence() {
		if (unavailable()) return;
		var esthree = this.esthree();
		assertThat(esthree.existsBucket("example-123")).isFalse();
	}

	@Test @Order(7)
	@DisplayName("Esthree successfully mass-creates 2000 buckets")
	void createBuckets() {
		if (unavailable()) return;
		assertThatNoException().isThrownBy(() -> {
			var esthree = this.esthree();
			for (int i = 0; i < 2000; i++) esthree.createBucket("example-" + i);
		});
	}

	@Test @Order(8)
	@DisplayName("Esthree successfully lists 2000 buckets with pagination and prefix limiting")
	void listBuckets() {
		if (unavailable()) return;
		assertThatNoException().isThrownBy(() -> {
			var esthree = this.esthree();
			var yesterday = Instant.now().minus(24, HOURS);

			var expected = range(0, 2000)
					.mapToObj(index -> "example-" + index)
					.collect(toList());

			assertThat(esthree.buckets("example-")
					.filter(bucket -> bucket.creation().isAfter(yesterday))
					.map(Esthree.Bucket::name)
					.collect(toList()))
					.containsExactlyInAnyOrderElementsOf(expected);
		});
	}

	@Test @Order(9)
	@DisplayName("Esthree successfully mass-deletes 2000 buckets")
	void deleteBuckets() {
		if (unavailable()) return;
		assertThatNoException().isThrownBy(() -> {
			var esthree = this.esthree();
			for (int i = 0; i < 2000; i++) esthree.deleteBucket("example-" + i);
		});
	}

	@Test @Order(10)
	@DisplayName("Esthree does not expose credentials in stacktrace")
	void doesNotExposeCredentialsStacktrace() {
		if (unavailable()) return;

		var esthree = this.esthree();
		var signer = (DEsthreeSigner) this.esthreeSigner();

		assertThatThrownBy(() -> esthree.deleteBucket("example-123"))
				.isInstanceOf(EsthreeException.class)
				.extracting(throwable -> {
					var writer = new StringWriter();
					throwable.printStackTrace(new PrintWriter(writer));
					return writer.toString();
				})
				.satisfies(message -> {
					assertThat(message).doesNotContain("minioadmin");
					assertThat(message).doesNotContain(signer.hex(signer.sha256(new byte[0])));
				});
	}
}