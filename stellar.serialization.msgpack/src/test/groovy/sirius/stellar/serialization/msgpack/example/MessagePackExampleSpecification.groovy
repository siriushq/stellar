package sirius.stellar.serialization.msgpack.example

import spock.lang.Specification

class MessagePackExampleSpecification extends Specification {

    def "have basic usage"() {
      MessagePackExample.basicUsage()
    }

    def "have packer usage"() {
      MessagePackExample.packer()
    }

    def "have file read and write example"() {
      MessagePackExample.readAndWriteFile();
    }

    def "have configuration example"() {
      MessagePackExample.configuration();
    }
}