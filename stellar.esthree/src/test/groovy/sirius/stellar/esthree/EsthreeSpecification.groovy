package sirius.stellar.esthree

import io.avaje.http.client.HttpException
import spock.lang.Specification

import static sirius.stellar.esthree.Esthree.Region.*

class EsthreeSpecification extends Specification {

	def esthreeSigner = EsthreeSigner.create("minioadmin", "minioadmin", US_EAST_2)
	def esthree = Esthree.builder()
			.region(US_EAST_2)
			.endpoint("http://127.0.0.1:9000", false)
			.credentials("minioadmin", "minioadmin")
			.build()

	def "Esthree does not fail during instantiation"() {
		when: Esthree.builder()
				.credentials("example", "example")
				.build()
		then: notThrown IllegalStateException
	}

	def "Esthree fails with credential-free instantiation"() {
		when: Esthree.builder().build()
		then: thrown IllegalStateException
	}

	def "Esthree successfully creates bucket"() {
		when: esthree.createBucket("example-123")
		then: notThrown HttpException
	}

	def "Esthree successfully checks existence of bucket"() {
		expect: esthree.existsBucket("example-123")
	}

	def "Esthree successfully deletes bucket"() {
		when: esthree.deleteBucket("example-123")
		then: notThrown HttpException
	}

	def "Esthree successfully checks non-existence of bucket"() {
		expect: !esthree.existsBucket("example-123")
	}

	def "Esthree does not expose credentials in stacktrace"() {
		when:
			def signer = esthreeSigner as DEsthreeSigner
			esthree.deleteBucket("example-123")
		then:
			def exception = thrown HttpException
			!exception.message.contains("minioadmin")
			!exception.message.contains(signer.hex(signer.sha256(new byte[0])))
	}
}