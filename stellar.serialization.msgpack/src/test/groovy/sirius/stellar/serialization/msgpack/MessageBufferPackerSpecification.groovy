package sirius.stellar.serialization.msgpack

import sirius.stellar.serialization.msgpack.value.ValueFactory
import spock.lang.Specification

class MessageBufferPackerSpecification extends Specification {

	def "MessageBufferPacker should be equivalent to ByteArrayOutputStream"() {
		given: "A buffer packer and a stream packer writing the same data"
			def packer1 = MessagePack.newDefaultBufferPacker()
			packer1.packValue(ValueFactory.newMap(
					ValueFactory.newString("a"), ValueFactory.newInteger(1),
					ValueFactory.newString("b"), ValueFactory.newString("s")
			))

			def stream = new ByteArrayOutputStream()
			def packer2 = MessagePack.newDefaultPacker(stream)
			packer2.packValue(ValueFactory.newMap(
					ValueFactory.newString("a"), ValueFactory.newInteger(1),
					ValueFactory.newString("b"), ValueFactory.newString("s")
			))
			packer2.flush()

		expect: "Both packers produce identical bytes"
			packer1.toByteArray() == stream.toByteArray()
	}

	def "MessageBufferPacker should clear unflushed data correctly"() {
		given: "A buffer packer with pending data"
			def packer = MessagePack.newDefaultBufferPacker()
			packer.packInt(1)
			packer.clear()
			packer.packInt(2)

		when: "Getting output after clear"
			def bytes = packer.toByteArray()
			def buffer = packer.toBufferList().get(0)
			def bufferBytes = buffer.toByteArray()
			def array = Arrays.copyOf(buffer.sliceAsByteBuffer().array(), buffer.size())

		then: "Only the final packed value remains (2)"
			bytes == [(byte) 2] as byte[]
			bufferBytes == [(byte) 2] as byte[]
			array == [(byte) 2] as byte[]
	}
}