// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack

import sirius.stellar.serialization.msgpack.exception.MessageSizeException
import sirius.stellar.serialization.msgpack.value.Variable
import spock.lang.Specification

class StringLimitSpecification extends Specification {

    int customLimit = 100
    byte[] oversizedStringMsgpack

    def setup() {
        def packer = MessagePack.newDefaultBufferPacker()
        packer.packString("a" * (customLimit + 1))
        oversizedStringMsgpack = packer.toByteArray()
    }

    private def unpacker() {
        return new MessagePack.UnpackerConfig()
                .stringSizeLimit(customLimit)
                .newUnpacker(oversizedStringMsgpack)
    }

    def "unpackString throws MessageSizeException when string exceeds limit"() {
        when:
        	unpacker().unpackString()
        then:
        	thrown(MessageSizeException)
    }

    def "unpackValue throws MessageSizeException when string exceeds limit"() {
        when:
        	unpacker().unpackValue()
        then:
        	thrown(MessageSizeException)
    }

    def "unpackValue with Variable throws MessageSizeException when string exceeds limit"() {
        given:
        	def variable = new Variable()
        when:
        	unpacker().unpackValue(variable)
        then:
        	thrown(MessageSizeException)
    }
}