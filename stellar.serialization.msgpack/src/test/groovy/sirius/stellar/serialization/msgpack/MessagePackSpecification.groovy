package sirius.stellar.serialization.msgpack

import sirius.stellar.serialization.msgpack.exception.MessageIntegerOverflowException
import sirius.stellar.serialization.msgpack.exception.MessageTypeException
import spock.lang.Specification

import java.time.Instant

import static java.nio.charset.CodingErrorAction.*
import static sirius.stellar.serialization.msgpack.MessagePack.*

class MessagePackSpecification extends Specification {

	static def packerConfig = new PackerConfig()
	static def unpackerConfig = new UnpackerConfig()
	static def random = new Random()

	static def shortMin = Short.MIN_VALUE, shortMax = Short.MAX_VALUE;
	static def byteMin = Byte.MIN_VALUE, byteMax = Byte.MAX_VALUE;
	static def intMin = Integer.MIN_VALUE, intMax = Integer.MAX_VALUE;

	private static <T> boolean check(T t, Closure pack, Closure unpack) {
		ByteArrayOutputStream output = new ByteArrayOutputStream()

		def packer = packerConfig.newPacker(output)
		pack(packer)
		packer.close()
		byte[] bytes = output.toByteArray()

		def unpacker = unpackerConfig.newUnpacker(bytes)
		def result = unpack(unpacker)
		assert result == t

		return true
	}

	private static <T> void checkException(T t, Closure pack, Closure unpack) {
		ByteArrayOutputStream output = new ByteArrayOutputStream()

		def packer = packerConfig.newPacker(output)
		pack(packer)
		packer.close()

		byte[] bytes = output.toByteArray()
		def unpacker = unpackerConfig.newUnpacker(bytes)
		unpack(unpacker)

		assert false: "should not reach here"
	}

	private static <T> void checkOverflow(T t, Closure pack, Closure unpack) {
		try {
			checkException(t, pack, unpack)
			assert false: "should not reach here"
		} catch (MessageIntegerOverflowException ignored) {
		}
	}

	def "clone packer config"() {
		given:
			def config = new PackerConfig()
					.bufferSize(10)
					.bufferFlushThreshold(32 * 1024)
					.smallStringOptimizationThreshold(142)
		when:
			def copy = config.clone()
		then:
			copy == config
	}

	def "clone unpacker config"() {
		given:
			def config = new UnpackerConfig()
					.bufferSize(1)
					.actionOnMalformedString(IGNORE)
					.actionOnUnmappableString(REPORT)
					.allowReadingBinaryAsString(false)
					.stringDecoderBufferSize(34)
					.stringSizeLimit(4324)
		when:
			def copy = config.clone()
		then:
			copy == config
	}

	def "detect fixarray and fixmap values"() {
		given:
			def packer = newDefaultBufferPacker()
		when:
			packer.packArrayHeader(0)
			packer.close()
			def bytes = packer.toByteArray()

		then:
			newDefaultUnpacker(bytes).unpackArrayHeader() == 0
		when:
			newDefaultUnpacker(bytes).unpackMapHeader()
		then:
			thrown(MessageTypeException)
	}

	def "detect pos fix int values"() {
		expect:
		(0x00..<0x80).every { Code.isPosFixInt(it as byte) }
		(0x80..<0x100).every { !Code.isPosFixInt(it as byte) }
	}

	def "detect neg fix int values"() {
		expect:
		(0x00..<0xe0).every { !Code.isNegFixInt(it as byte) }
		(0xe0..<0x100).every { Code.isNegFixInt(it as byte) }
	}

	def "pack/unpack primitive values"() {
		expect:
		check(true, { it.packBoolean(true) }, { it.unpackBoolean() })
		check((byte) 1, { it.packByte((byte) 1) }, { it.unpackByte() })
		check((short) 2, { it.packShort((short) 2) }, { it.unpackShort() })
		check(42, { it.packInt(42) }, { it.unpackInt() })
		check(42L, { it.packLong(42L) }, { it.unpackLong() })
		check(3.14f, { it.packFloat(3.14f) }, { it.unpackFloat() })
		check(2.718, { it.packDouble(2.718) }, { it.unpackDouble() })
		check(null, { it.packNil() }, { it.unpackNil(); null })
	}

	def "pack/unpack integer values"() {
		expect:
			check(value, { it.packLong(value) }, { it.unpackLong() })

			if (value >= shortMin && value <= shortMax && value >= byteMin && value <= byteMax) {
				check((byte) value, { it.packByte((byte) value) }, { it.unpackByte() })
				check((short) value, { it.packShort((short) value) }, { it.unpackShort() })
			}

			if (value >= intMin && value <= intMax) {
				check((int) value, { it.packInt((int) value) }, { it.unpackInt() })
			} else {
				checkOverflow(value, { it.packLong(value) }, { it.unpackInt() })
			}

			if (value < shortMin || value > shortMax) {
				checkOverflow(value, { it.packLong(value) }, { it.unpackShort() })
			}

			if (value < byteMin || value > byteMax) {
				checkOverflow(value, { it.packLong(value) }, { it.unpackByte() })
			}
		where:
			_ | value                 | _

			_ | (long) (intMin - 10L) | _
			_ | -65535L               | _
			_ | -8191L                | _
			_ | -1024L                | _
			_ | -255L                 | _
			_ | -127L                 | _
			_ | -63L                  | _
			_ | -31L                  | _
			_ | -15L                  | _
			_ | -7L                   | _
			_ | -3L                   | _
			_ | -1L                   | _
			_ | 0L                    | _
			_ | 2L                    | _
			_ | 4L                    | _
			_ | 8L                    | _
			_ | 16L                   | _
			_ | 32L                   | _
			_ | 64L                   | _
			_ | 128L                  | _
			_ | 256L                  | _
			_ | 1024L                 | _
			_ | 8192L                 | _
			_ | 65536L                | _
			_ | (long) (intMax + 10L) | _
	}

	def "pack/unpack BigInteger"() {
		expect:
		def value = BigInteger.valueOf(123456789L)
		check(value, { it.packBigInteger(value) }, { it.unpackBigInteger() })
	}

	def "pack/unpack binary"() {
		given:
			def bytes = new byte[100]
			random.nextBytes(bytes)

		expect:
			check(bytes, {
				it.packBinaryHeader(bytes.length)
				it.writePayload(bytes)
			},{
				int length = it.unpackBinaryHeader()
				def output = new byte[length]
				it.readPayload(output, 0, length)
				output
			})
	}

	def "pack/unpack timestamp"() {
		given:
			def instant = Instant.ofEpochSecond(1700000000L, 123456789)
		expect:
			check(instant, { it.packTimestamp(instant) }, { it.unpackTimestamp() })
	}

	def "MessagePack.PackerConfig should be immutable"() {
		given:
			def config = new PackerConfig()
		when:
			def modified = config.bufferSize(64 * 1024)
		then:
			config != modified
	}

	def "MessagePack.UnpackerConfig should be immutable"() {
		given:
			def config = new UnpackerConfig()
		when:
			def modified = config.bufferSize(64 * 1024)
		then:
			config != modified
	}
}