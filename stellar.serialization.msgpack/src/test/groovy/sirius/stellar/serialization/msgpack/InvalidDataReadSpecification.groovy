package sirius.stellar.serialization.msgpack

import sirius.stellar.serialization.msgpack.exception.MessageInsufficientBufferException
import spock.lang.Specification

class InvalidDataReadSpecification extends Specification {

    def "Reading long EXT32"() {
        given: "An EXT32 header with an unrealistically large body size (2GB)"
			def output = new ByteArrayOutputStream()
			def packer = MessagePack.newDefaultPacker(output)
			packer.packExtensionTypeHeader(MessagePack.Code.EXT32, Integer.MAX_VALUE)
			packer.close()
			byte[] msgpack = output.toByteArray()

        and: "An unpacker for the fake EXT32 message"
        	def unpacker = MessagePack.newDefaultUnpacker(msgpack)

        when: "Trying to skip the EXT32 value without an actual body"
        	unpacker.skipValue()

        then: "MessageInsufficientBufferException should be thrown"
        	thrown(MessageInsufficientBufferException)

        cleanup:
        	unpacker?.close()
    }
}