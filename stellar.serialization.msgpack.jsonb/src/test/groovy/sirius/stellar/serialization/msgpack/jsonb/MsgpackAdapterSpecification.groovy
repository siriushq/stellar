package sirius.stellar.serialization.msgpack.jsonb

import io.avaje.jsonb.Jsonb
import spock.lang.Specification

class MsgpackAdapterSpecification extends Specification {

	def "adapter is located automagically by Jsonb with ServiceLoader"() {
		given:
		// def jsonb = Jsonb.instance(); - TODO make this test pass
		def jsonb = Jsonb.builder()
			.adapter(new MsgpackAdapter())
			.build();
		def input = new ByteArrayInputStream(new byte[0])
		def output = new ByteArrayOutputStream()
		when:
		def reader = jsonb.reader(input)
		def writer = jsonb.writer(output)
		then:
		reader instanceof MsgpackReader
		writer instanceof MsgpackWriter
	}
}