package sirius.stellar.serialization.msgpack.example

import sirius.stellar.serialization.msgpack.MessageFormat
import sirius.stellar.serialization.msgpack.MessagePack
import sirius.stellar.serialization.msgpack.MessageUnpacker
import sirius.stellar.serialization.msgpack.value.*

import java.time.Instant

class MessagePackExample {

    static def basicUsage() {
        // Serialize
        def packer = MessagePack.newDefaultBufferPacker()
        packer.packInt(1)
              .packString('leo')
              .packArrayHeader(2)
              .packString('xxx-xxxx')
              .packString('yyy-yyyy')
        packer.close()

        // Deserialize
        def unpacker = MessagePack.newDefaultUnpacker(packer.toByteArray())

        int id = unpacker.unpackInt()
        String name = unpacker.unpackString()
        int numPhones = unpacker.unpackArrayHeader()
        String[] phones = (0..<numPhones).collect { unpacker.unpackString() }
        unpacker.close()

        println "id:${id}, name:${name}, phone:[${phones.join(', ')}]"
    }

    static def packer() {
        def packer = MessagePack.newDefaultBufferPacker()

        packer.packBoolean(true)
        packer.packShort((short) 34)
        packer.packInt(1)
        packer.packLong(33000000000L)
        packer.packFloat(0.1f)
        packer.packDouble(3.14159263)
        packer.packByte((byte) 0x80)
        packer.packNil()
        packer.packString('hello message pack!')

        byte[] s = 'utf-8 strings'.getBytes(MessagePack.UTF8)
        packer.packRawStringHeader(s.length)
        packer.writePayload(s)

        int[] arr = [3, 5, 1, 0, -1, 255]
        packer.packArrayHeader(arr.length)
        arr.each { packer.packInt(it) }

        packer.packMapHeader(2)
        packer.packString('apple').packInt(1)
        packer.packString('banana').packInt(2)

        byte[] ba = [1, 2, 3, 4] as byte[]
        packer.packBinaryHeader(ba.length)
        packer.writePayload(ba)

        byte[] extData = 'custom data type'.getBytes(MessagePack.UTF8)
        packer.packExtensionTypeHeader((byte) 1, 10)
        packer.writePayload(extData)

        packer.packTimestamp(Instant.now())

        packer.packInt(1)
              .packString('leo')
              .packArrayHeader(2)
              .packString('xxx-xxxx')
              .packString('yyy-yyyy')
    }

    static void readAndWriteFile() {
        def tempFile = File.createTempFile('target/tmp', '.txt')
        tempFile.deleteOnExit()

        def packer = MessagePack.newDefaultPacker(new FileOutputStream(tempFile))
        packer.packInt(1)
              .packString('Hello Message Pack!')
              .packArrayHeader(2)
              .packFloat(0.1f)
              .packDouble(0.342)
        packer.close()

        def unpacker = MessagePack.newDefaultUnpacker(new FileInputStream(tempFile))
        while (unpacker.hasNext()) {
            MessageFormat format = unpacker.nextFormat()
			Value v = unpacker.unpackValue()

            switch (v.valueType) {
                case Value.Type.NIL:
                    println 'read nil'
                    break
                case Value.Type.BOOLEAN:
                    println "read boolean: ${v.asBooleanValue().boolean}"
                    break
                case Value.Type.INTEGER:
					IntegerValue iv = v.asIntegerValue()
                    if (iv.inIntRange) {
                        println "read int: ${iv.toInt()}"
                    } else if (iv.inLongRange) {
                        println "read long: ${iv.toLong()}"
                    } else {
                        println "read big integer: ${iv.toBigInteger()}"
                    }
                    break
                case Value.Type.FLOAT:
					FloatValue fv = v.asFloatValue()
                    println "read float: ${fv.toDouble()}"
                    break
                case Value.Type.STRING:
                    println "read string: ${v.asStringValue().asString()}"
                    break
                case Value.Type.BINARY:
                    byte[] mb = v.asBinaryValue().asByteArray()
                    println "read binary: size=${mb.length}"
                    break
                case Value.Type.ARRAY:
					ArrayValue a = v.asArrayValue()
                    a.each { println "read array element: ${it}" }
                    break
                case Value.Type.EXTENSION:
					ExtensionValue ev = v.asExtensionValue()
                    if (ev.timestampValue) {
                        Instant tsValue = ev.asTimestampValue().toInstant()
                        println "read timestamp: $tsValue"
                    } else {
                        byte extType = ev.type
                        byte[] extValue = ev.data
                        println "read extension: type=$extType, length=${extValue.length}"
                    }
                    break
            }
        }
        unpacker.close()
    }

    static void configuration() {
        def packer = new MessagePack.PackerConfig()
                .withSmallStringOptimizationThreshold(256)
                .newBufferPacker()
        packer.packInt(10)
        packer.packBoolean(true)
        packer.close()

        byte[] packedData = packer.toByteArray()
		MessageUnpacker unpacker = new MessagePack.UnpackerConfig()
                .withStringDecoderBufferSize(16 * 1024)
                .newUnpacker(packedData)
        int i = unpacker.unpackInt()
        boolean b = unpacker.unpackBoolean()
        unpacker.close()
    }
}