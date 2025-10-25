// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack

import sirius.stellar.serialization.msgpack.buffer.*
import sirius.stellar.serialization.msgpack.value.ValueType
import spock.lang.Specification

import java.nio.ByteBuffer

import static java.util.Objects.*
import static sirius.stellar.serialization.msgpack.MessageFormat.*

class MessageUnpackerSpecification { //extends Specification { TODO - fix & re-enable spec later...

    def universal = MessageBuffer.allocate(0) instanceof MessageBufferU
    def random = new Random()

	def intSequence = (0..<100).collect { random.nextInt() } as int[]

	def "parse message packed data"() {
		given:
			def arr = testData()
		when:
			def counts = unpackers(arr).collect { unpacker ->
				def count = 0
				while (unpacker.hasNext()) {
					count++
					readValue(unpacker)
				}
				def total = unpacker.totalReadBytes
				unpacker.close()
				[count: count, total: total]
			}
		then:
			counts.every { it.count == 6 && it.total == arr.length }
	}

	def "skip reading values"() {
		expect:
		unpackers(testData()).every { unpacker ->
			def skipCount = 0
			while (unpacker.hasNext()) {
				unpacker.skipValue()
				skipCount++
			}
			def result = skipCount == 2 && unpacker.totalReadBytes == testData.length
			unpacker.close()
			result && unpacker.totalReadBytes == testData.length
		}
	}

	def "parse int data"() {
		given:
			def expectedInts = intSequence
		expect:
			unpackers(testData2()).every { unpacker ->
				def ints = []
				while (unpacker.hasNext()) {
					def fmt = unpacker.nextFormat
					switch (fmt.valueType) {
						case ValueType.INTEGER:
							ints << unpacker.unpackInt()
							break
						case ValueType.BOOLEAN:
							unpacker.unpackBoolean()
							break
						default:
							unpacker.skipValue()
					}
				}
				def ok = ints == expectedInts.toList()
				unpacker.close()
				ok && unpacker.totalReadBytes == testData2.length
			}
	}

	def "read data at the buffer boundary"() {
		expect:
		[testData(), testData3(30)].each { data ->
			unpackers(data).each { unpacker ->
				def numElems = 0
				while (unpacker.hasNext()) {
					readValue(unpacker)
					numElems++
				}
				(1..<data.length - 1).each { splitPoint ->
					def (head, tail) = [Arrays.copyOfRange(data, 0, splitPoint), Arrays.copyOfRange(data, splitPoint, data.length)]
					def bin = new SplitMessageBufferInput([head, tail] as byte[][])
					def u = MessagePack.newDefaultUnpacker(bin)
					def count = 0
					while (u.hasNext()) {
						u.nextFormat
						readValue(u)
						count++
					}
					assert count == numElems
					assert u.totalReadBytes == data.length
					u.close()
					assert u.totalReadBytes == data.length
				}
			}
		}
	}

	def "read integer at MessageBuffer boundaries"() {
		given:
			def packer = MessagePack.newDefaultBufferPacker()
			(0..<1170).each { packer.packLong(0x0011223344556677L) }
			packer.close()
			def data = packer.toByteArray()
		expect:
			def streamUnpacker = MessagePack.newDefaultUnpacker(new InputStreamBufferInput(new ByteArrayInputStream(data), 8192))
			(0..<1170).each { assert streamUnpacker.unpackLong() == 0x0011223344556677L }
			streamUnpacker.close()

			unpackerCollectionWithVariousBuffers(data, 32).each { sequenceUnpacker ->
				(0..<1170).each { assert sequenceUnpacker.unpackLong() == 0x0011223344556677L }
				sequenceUnpacker.close()
			}
	}

	def "read string at MessageBuffer boundaries"() {
		given:
			def packer = MessagePack.newDefaultBufferPacker()
			(0..<1170).each { packer.packString("hello world") }
			packer.close()
			def data = packer.toByteArray()
		expect:
			def streamUnpacker = MessagePack.newDefaultUnpacker(new InputStreamBufferInput(new ByteArrayInputStream(data), 8192))
			(0..<1170).each { assert streamUnpacker.unpackString() == "hello world" }
			streamUnpacker.close()

			unpackerCollectionWithVariousBuffers(data, 32).each { sequenceUnpacker ->
				(0..<1170).each { assert sequenceUnpacker.unpackString() == "hello world" }
				sequenceUnpacker.close()
			}
	}

	def "read payload as a reference"() {
		given:
			def sizes = [0, 1, 5, 8, 16, 32, 128, 256, 1024, 2000, 10000, 100000]
		expect:
			sizes.each { size ->
				def data = new byte[size]
				new Random().nextBytes(data)
				def output = new ByteArrayOutputStream()
				def packer = MessagePack.newDefaultPacker(output)
				packer.packBinaryHeader(size)
				packer.writePayload(data)
				packer.close()
				unpackers(output.toByteArray()).each { unpacker ->
					def len = unpacker.unpackBinaryHeader()
					assert len == size
					def ref = unpacker.readPayloadAsReference(len)
					unpacker.close()
					assert ref.size() == size
					def stored = new byte[len]
					ref.getBytes(0, stored, 0, len)
					assert Arrays.equals(stored, data)
				}
			}
	}

	def "reset the internal states"() {
		given:
			def data = intSequence
			def bytes = createMessagePackData { packer -> data.each { packer.packInt(it) } }
		expect:
			unpackers(bytes).each { unpacker ->
				def unpacked = []
				while (unpacker.hasNext()) unpacked << unpacker.unpackInt()
				unpacker.close()
				assert unpacked == data

				def data2 = intSequence
				def b2 = createMessagePackData { p -> data2.each { p.packInt(it) } }
				def bi = new ArrayBufferInput(b2)
				unpacker.reset(bi)
				def unpacked2 = []
				while (unpacker.hasNext()) unpacked2 << unpacker.unpackInt()
				unpacker.close()
				assert unpacked2 == data2

				bi.reset(b2)
				unpacker.reset(bi)
				def unpacked3 = []
				while (unpacker.hasNext()) unpacked3 << unpacker.unpackInt()
				unpacker.close()
				assert unpacked3 == data2
			}
	}

	def "reset ChannelBufferInput"() {
		given:
			def file = createTempFile()
			def unpacker = MessagePack.newDefaultUnpacker(new FileInputStream(file).channel)
		when:
			checkFile(unpacker)
			def file2 = createTempFile()
			def channel = new FileInputStream(file2).channel
			unpacker.reset(new ChannelBufferInput(channel))
			checkFile(unpacker)
			unpacker.close()

		then:
		noExceptionThrown()
	}

	def "reset InputStreamBufferInput"() {
		given:
			def file = createTempFile()
			def unpacker = MessagePack.newDefaultUnpacker(new FileInputStream(file))
		when:
			checkFile(unpacker)
			def file1 = createTempFile()
			def input = new FileInputStream(file1)
			unpacker.reset(new InputStreamBufferInput(input))
			checkFile(unpacker)
			unpacker.close()
		then:
			noExceptionThrown()
	}

	def "unpack large string data"() {
		expect:
			[8191, 8192, 8193, 16383, 16384, 16385].each { n ->
				def out = new ByteArrayOutputStream()
				def packer = MessagePack.newDefaultPacker(out)
				packer.packArrayHeader(2)
						.packString("l" * n)
						.packInt(1)
				packer.close()
				def array = out.toByteArray()

				unpackers(array).each { unpacker ->
					assert unpacker.unpackArrayHeader() == 2
					assert unpacker.unpackString().length() == n
					assert unpacker.unpackInt() == 1
					assert unpacker.totalReadBytes == array.length
					unpacker.close()
					assert unpacker.totalReadBytes == array.length
				}
			}
	}

	def "unpack string crossing end of buffer"() {
		expect:
		def check = { expected, strLen ->
			def bytes = new byte[strLen]
			def out = new ByteArrayOutputStream()
			def packer = MessagePack.newDefaultPacker(out)
			packer.packBinaryHeader(bytes.length)
			packer.writePayload(bytes)
			packer.packString(expected)
			packer.close()

			def unpacker = MessagePack.newDefaultUnpacker(new InputStreamBufferInput(new ByteArrayInputStream(out.toByteArray())))
			def length = unpacker.unpackBinaryHeader()
			unpacker.readPayload(length)
			def got = unpacker.unpackString()
			unpacker.close()
			assert got == expected
		}

		[ [0x3042], [0x61, 0x3042], [0x3042, 0x61], [0x3042, 0x3044, 0x3046, 0x3048, 0x304A, 0x304B, 0x304D, 0x304F, 0x3051, 0x3053, 0x3055, 0x3057, 0x3059, 0x305B, 0x305D] ]
				.collect { new String(it as int[], 0, it.size()) }
				.each { s ->
					[8185, 8186, 8187, 8188, 16377, 16378, 16379, 16380].each { n ->
						check(s, n)
					}
				}
	}

	def "read value length at buffer boundary"() {
		expect:
		def input1 = new SplitMessageBufferInput([
				[MessagePack.Code.STR16] as byte[],
				[0x00] as byte[],
				[0x05] as byte[],
				"hello".bytes
		] as byte[][])
		readTest(input1)

		def input2 = new SplitMessageBufferInput([
				[MessagePack.Code.STR32] as byte[],
				[0x00] as byte[],
				[0x00, 0x00] as byte[],
				[0x05] as byte[],
				"hello".bytes
		] as byte[][])
		readTest(input2)
	}

	//#region testData*
	def testData() {
        def output = new ByteArrayOutputStream()
        def packer = MessagePack.newDefaultPacker(output)

        packer.packArrayHeader(2)
              .packInt(1)
              .packString("leo")
              .packArrayHeader(2)
              .packInt(5)
              .packString("aina")
        packer.close()

        def array = output.toByteArray()
		println("packed: ${toHex(array)}, size: ${array.length}")

        return array
	}

	def testData2() {
        def output = new ByteArrayOutputStream()
        def packer = MessagePack.newDefaultPacker(output)

        packer.packBoolean(true)
              .packBoolean(false)

        intSequence.each { packer.packInt(it) }
        packer.close()

        def array = output.toByteArray()
        println("packed: ${toHex(array)}")
        return array
    }

	def testData3(int n) {
        def output = new ByteArrayOutputStream()
        def packer = MessagePack.newDefaultPacker(output)

        (0..<n).each { write(packer) }

        packer.close()
        def array = output.toByteArray()
        println("packed: ${toHex(array)}")
        println("size: ${array.length}")
        return array
    }
	//#endregion

	//#region utility methods
	def toHex(byte[] arr) {
        return arr.collect { String.format("%02x", it) }.join(' ')
    }

	def createMessagePackData(Closure closure) {
		def output = new ByteArrayOutputStream()
		def packer = MessagePack.newDefaultPacker(output)

		closure.call(packer)
		packer.close()

		return output.toByteArray()
	}

	def write(MessagePacker packer) {
		def formats = MessageFormat.values().findAll({ it != NEVER_USED })
		def format = formats[random.nextInt(formats.size())]
		ValueType valueType = format.getValueType()

		switch (valueType) {
			case ValueType.INTEGER:
				int intValue = random.nextInt(Integer.MAX_VALUE)
				println("int: ${intValue}")

				packer.packInt(intValue)
				break
			case ValueType.FLOAT:
				float floatValue = random.nextFloat()
				println("float: ${floatValue}")

				packer.packFloat(floatValue)
				break
			case ValueType.BOOLEAN:
				boolean boolValue = random.nextBoolean()
				println("boolean: ${boolValue}")

				packer.packBoolean(boolValue)
				break
			case ValueType.STRING:
				int strLength = random.nextInt(100)
				String randomString = random
						.ints('a' as int, 'z' as int + 1)
						.limit(strLength)
						.collect(StringBuilder::new, StringBuilder.&appendCodePoint, StringBuilder.&append)
						.toString()
				println("string: ${randomString}")

				packer.packString(randomString)
				break
			case ValueType.BINARY:
				int byteLength = random.nextInt(100)
				byte[] byteArray = new byte[byteLength]
				random.nextBytes(byteArray)
				println("binary: ${toHex(byteArray)}")

				packer.packBinaryHeader(byteArray.length)
				packer.writePayload(byteArray)

				break
			case ValueType.ARRAY:
				int arrayLength = random.nextInt(5)
				println("array length: ${arrayLength}")

				packer.packArrayHeader(arrayLength)
				for (int i = 0; i < arrayLength; i++) write(packer)

				break
			case ValueType.MAP:
				int mapLength = random.nextInt(5) + 1
				println("map length: ${mapLength}")

				packer.packMapHeader(mapLength)
				for (int i = 0; i < mapLength * 2; i++) write(packer)
				break
			default:
				int fallbackValue = random.nextInt(Integer.MAX_VALUE)
				println("default int: ${fallbackValue}")

				packer.packInt(fallbackValue)
				break
		}
	}

    def readValue(MessageUnpacker unpacker) {
        def format = unpacker.getNextFormat()
        def valueType = format.getValueType()

        switch (valueType) {
            case ValueType.ARRAY:
                def arrLen = unpacker.unpackArrayHeader()
                println("arr size: ${arrLen}")
                break
            case ValueType.MAP:
                def mapLen = unpacker.unpackMapHeader()
                println("map size: ${mapLen}")
                break
            case ValueType.INTEGER:
                def i = unpacker.unpackLong()
                println("int value: ${i}")
                break
            case ValueType.STRING:
                def s = unpacker.unpackString()
                println("str value: ${s}")
                break
            default:
                unpacker.skipValue()
                println("unknown type: ${format}")
                break
        }
    }

	def createTempFile() {
        def file = File.createTempFile("msgpackTest", "msgpack")
        file.deleteOnExit()

        def packer = MessagePack.newDefaultPacker(new FileOutputStream(file))
        packer.packInt(99)
        packer.close()

        return file
    }

	def checkFile(MessageUnpacker unpacker) {
        def value = unpacker.unpackInt()
        assert value == 99
        assert !unpacker.hasNext()
        return true
    }

	def unpackers(byte[] data) {
        def byteBuffer = ByteBuffer.allocate(data.length)
        def directBuffer = ByteBuffer.allocateDirect(data.length)

        byteBuffer.put(data)
        byteBuffer.flip()
        directBuffer.put(data)
        directBuffer.flip()

        List<MessageUnpacker> unpackers = []
        unpackers << MessagePack.newDefaultUnpacker(data)
        unpackers << MessagePack.newDefaultUnpacker(byteBuffer)

        if (!universal) unpackers << MessagePack.newDefaultUnpacker(directBuffer)
        return unpackers
    }

	def unpackerCollectionWithVariousBuffers(byte[] data, int chunkSize) {
		def seqBytes = []
		def seqByteBuffers = []
		def seqDirectBuffers = []

		int left = data.length
		int position = 0

		while (left > 0) {
			int length = Math.min(chunkSize, left)

			seqBytes << new ArrayBufferInput(data, position, length)

			def byteBuffer = ByteBuffer.allocate(length)
			def directBuffer = ByteBuffer.allocateDirect(length)
			byteBuffer.put(data, position, length).flip()
			directBuffer.put(data, position, length).flip()

			seqByteBuffers << new ByteBufferInput(byteBuffer)
			seqDirectBuffers << new ByteBufferInput(directBuffer)

			left -= length
			position += length
		}

		MessageUnpacker[] unpackers = []
		if (universal) {
			unpackers << MessagePack.newDefaultUnpacker(new SequenceMessageBufferInput(Collections.enumeration(seqBytes)))
			unpackers << MessagePack.newDefaultUnpacker(new SequenceMessageBufferInput(Collections.enumeration(seqByteBuffers)))
			return unpackers
		}

		unpackers << MessagePack.newDefaultUnpacker(new SequenceMessageBufferInput(Collections.enumeration(seqDirectBuffers)))
		return unpackers
	}
	//#endregion
}

//#region MessageBufferInput implementations
class SplitMessageBufferInput implements MessageBufferInput {

	private byte[][] array
	private int cursor

	SplitMessageBufferInput(byte[][] array) {
		this.array = array
		this.cursor = 0
	}

	@Override
	MessageBuffer next() {
		if (this.cursor >= this.array.length) return null
		byte[] a = this.array[this.cursor++]
		return MessageBuffer.wrap(a)
	}

	@Override
	void close() {
		assert true
	}
}

class SequenceMessageBufferInput implements MessageBufferInput {

	private Enumeration<? extends MessageBufferInput> sequence
	private MessageBufferInput input

	SequenceMessageBufferInput(Enumeration<? extends MessageBufferInput> sequence) {
		this.sequence = requireNonNull(sequence, "input sequence is null")
		try {
			nextInput()
		} catch (IOException ignored) {
		}
	}

	@Override
	MessageBuffer next() throws IOException {
		if (this.input == null) return null

		def buffer = this.input.next()
		if (buffer == null) {
			nextInput()
			return next()
		}

		return buffer
	}

	private void nextInput() throws IOException {
		if (this.input != null) this.input.close()

		if (this.sequence.hasMoreElements()) {
			this.input = this.sequence.nextElement()
			if (this.input == null) throw new NullPointerException("An element in the MessageBufferInput sequence is null")
			return
		}

		this.input = null
	}

	@Override
	void close() throws IOException {
		do {
			nextInput()
		} while (this.input != null)
	}
}
//#endregion