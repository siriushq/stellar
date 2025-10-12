package sirius.stellar.serialization.msgpack.jsonb

import io.avaje.jsonb.Jsonb
import sirius.stellar.facility.Strings
import spock.lang.Specification

class MsgpackAdapterSpecification extends Specification {

	def "adapter is located automagically by Jsonb with ServiceLoader"() {
		given:
			def jsonb = Jsonb.instance();
		when:
			def reader = jsonb.reader(Strings.EMPTY)
			def writer = jsonb.writer(OutputStream.nullOutputStream())
		then:
			reader instanceof MsgpackReader
			writer instanceof MsgpackWriter
	}
}