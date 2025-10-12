package sirius.stellar.esthree

import io.avaje.http.client.HttpException
import spock.lang.Specification

class EsthreeSpecification extends Specification {

	def "Esthree does not fail during instantiation"() {
		when:
			def esthree = Esthree.builder()
					.endpoint("http://localhost:9000", false)
					.credentials("minioadmin", "minioadmin")
					.build();
		then:
			notThrown(IllegalStateException)
	}

	def "Esthree successfully creates bucket"() {
		given:
			def esthree = Esthree.builder()
						.endpoint("http://localhost:9000", false)
						.credentials("minioadmin", "minioadmin")
						.build();
		when:
			esthree.createBucket("example-123")
		then:
			notThrown(HttpException)
	}
}