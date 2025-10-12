package sirius.stellar.esthree

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
}