package sirius.stellar.facility

import spock.lang.Specification

class ThrowablesSpecification extends Specification {

	//#region forEach(Throwable)
	def "forEach(Throwable) correctly iterates over causes"() {
		given:
			def cause = new Throwable("A cause")
			def throwable = new Throwable(cause)
		when:
			def result = []
			Throwables.forEach(throwable, result::add)
		then:
			result.size() == 2
	}

	def "forEach(Throwable) correctly handles a recursive cause structure"() {
		given:
			def a = new Throwable()
			def b = new Throwable(a)
			a.initCause(b)
		when:
			def result = []
			Throwables.forEach(b, result::add)
		then:
			result.size() == 2
	}
	//#endregion

	//#region causes(Throwable)
	def "causes(Throwable) correctly returns a list of causes"() {
		given:
			def cause = new Throwable("A cause")
			def throwable = new Throwable(cause)
		when:
			def list = Throwables.causes(throwable)
		then:
			list.size() == 2
	}

	def "causes(Throwable) correctly handles a recursive cause structure"() {
		given:
			def a = new Throwable()
			def b = new Throwable(a)
			a.initCause(b)
		when:
			def list = Throwables.causes(b)
		then:
			list.size() == 2
	}
	//#endregion

	//#region stream(Throwable)
	def "stream(Throwable) correctly returns a stream of causes"() {
		given:
			def cause = new Throwable("A cause")
			def throwable = new Throwable(cause)
		when:
			def stream = Throwables.stream(throwable)
		then:
			stream.toList().size() == 2
	}

	def "stream(Throwable) correctly handles a recursive cause structure"() {
		given:
			def a = new Throwable()
			def b = new Throwable(a)
			a.initCause(b)
		when:
			def stream = Throwables.stream(b)
		then:
			stream.toList().size() == 2
	}

	def "stream(Throwable) correctly handles cycles in the cause chain tail"() {
		given:
			def a = new Throwable("A")
			def b = new Throwable("B")
			b.initCause(a)
			a.initCause(b)

			def root = new Throwable("Root")
			root.initCause(b)
		when:
			def stream = Throwables.stream(root)
			def list = stream.toList()
		then:
			list == [root, b, a]
			list.size() == 3
	}
	//#endregion

	//#region stacktrace(Throwable)
	def "stacktrace(Throwable) outputs a stacktrace when provided a throwable and is null-safe"() {
		given:
			def throwable = new Throwable()
		when:
			def a = Throwables.stacktrace(null)
			def b = Throwables.stacktrace(throwable)
		then:
			a == "null"
			b.startsWith("java.lang.Throwable")
	}
	//#endregion
}