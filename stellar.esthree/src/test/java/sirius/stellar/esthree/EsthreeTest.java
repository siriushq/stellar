package sirius.stellar.esthree;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Runtime.*;
import static java.lang.System.*;
import static java.lang.Thread.*;
import static java.net.HttpURLConnection.*;
import static java.time.temporal.ChronoUnit.*;
import static java.util.concurrent.Executors.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static sirius.stellar.esthree.Esthree.Region.*;

@TestMethodOrder(OrderAnnotation.class)
final class EsthreeTest {

	/// Username and password for accessing mock S3 server.
	String key = "minioadmin";

	/// Executor for parallel testing with large request counts.
	ExecutorService executor;

	Esthree esthree = Esthree.builder()
			.region(US_EAST_2)
			.endpoint("http://127.0.0.1:9000", false)
			.credentials(key, key)
			.build();

	@BeforeEach
	void setup() {
		executor = newFixedThreadPool(getRuntime().availableProcessors());
	}

	/// Returns `true` if the S3 server is not running, also sending an error message.
	boolean unavailable() {
		try {
			var url = URI.create("https://127.0.0.1:9000").toURL();

			var connection = (HttpURLConnection) url.openConnection();
			if (connection.getResponseCode() == HTTP_FORBIDDEN) return false;

			connection.getInputStream().readAllBytes();
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
			esthree.createBucket("example-123");
			esthree.createBucketFuture("example-123-future").get();
		});
	}

	@Test @Order(4)
	@DisplayName("Esthree successfully checks existence of bucket")
	void checkBucketExistence() {
		if (unavailable()) return;
		assertThatNoException().isThrownBy(() -> {
			assertThat(esthree.existsBucket("example-123")).isTrue();
			assertThat(esthree.existsBucketFuture("example-123-future").get()).isTrue();
		});
	}

	@Test @Order(5)
	@DisplayName("Esthree successfully deletes bucket")
	void deleteBucket() {
		if (unavailable()) return;
		assertThatNoException().isThrownBy(() -> {
			esthree.deleteBucket("example-123");
			esthree.deleteBucketFuture("example-123-future").get();
		});
	}

	@Test @Order(6)
	@DisplayName("Esthree successfully checks non-existence of bucket")
	void checksBucketNonExistence() {
		if (unavailable()) return;
		assertThatNoException().isThrownBy(() -> {
			assertThat(esthree.existsBucket("example-123")).isFalse();
			assertThat(esthree.existsBucketFuture("example-123-future").get()).isFalse();
		});
	}

	@Test @Order(7)
	@DisplayName("Esthree successfully mass-creates 2000 buckets in parallel")
	void createBuckets() {
		if (unavailable()) return;
		assertSoftly(softly -> {
			var counter = new AtomicInteger(0);
			range(0, 2000)
					.mapToObj(index -> "example-" + index)
					.forEach(name -> executor.execute(() -> {
						var error = catchThrowable(() -> esthree.createBucket(name));
						softly.assertThat(error).isNull();
						counter.incrementAndGet();
					}));

			executor.shutdown();
			while (!executor.isTerminated() && counter.get() < 2000 && !currentThread().isInterrupted()) {
				err.printf("\rcreated %s buckets...", counter.get());
				onSpinWait();
			}
			err.printf("\rcreation complete...%s\n", " ".repeat(20));
		});
	}

	@Test @Order(8)
	@DisplayName("Esthree successfully lists 2000 buckets with pagination and prefix limiting")
	void listBuckets() {
		if (unavailable()) return;
		assertThatNoException().isThrownBy(() -> {
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
	@DisplayName("Esthree successfully mass-deletes 2000 buckets in parallel")
	void deleteBuckets() {
		if (unavailable()) return;
		assertSoftly(softly -> {
			var counter = new AtomicInteger(0);
			range(0, 2000)
					.mapToObj(index -> "example-" + index)
					.forEach(name -> executor.execute(() -> {
						var error = catchThrowable(() -> esthree.deleteBucket(name));
						softly.assertThat(error).isNull();
						counter.incrementAndGet();
					}));

			executor.shutdown();
			while (!executor.isTerminated() && counter.get() < 2000 && !currentThread().isInterrupted()) {
				err.printf("\rdeleted %s buckets...", counter.get());
				onSpinWait();
			}
			err.printf("\rdeleting complete...%s\n", " ".repeat(20));
		});
	}

	@Test @Order(10)
	@DisplayName("Esthree does not expose credentials in stacktrace")
	void doesNotExposeCredentialsStacktrace() {
		if (unavailable()) return;
		assertThatThrownBy(() -> esthree.deleteBucket("example-123"))
				.isInstanceOf(EsthreeException.class)
				.extracting(throwable -> {
					var writer = new StringWriter();
					throwable.printStackTrace(new PrintWriter(writer));
					return writer.toString();
				})
				.satisfies(message -> {
					var signer = (DEsthreeSigner) EsthreeSigner.create(key, key, US_EAST_2);

					assertThat(message).doesNotContain("minioadmin");
					assertThat(message).doesNotContain(signer.hex(signer.sha256(new byte[0])));
				});
	}
}