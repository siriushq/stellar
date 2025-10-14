package sirius.stellar.facility

import spock.lang.Specification

class IteratorsSpecification extends Specification {

	def "from(T,,,) returns valid, resettable iterator starting from index 0"() {
		when:
			def iterator = Iterators.from("first", "second", "third")

			def a = iterator.next()
			def b = iterator.next()
			def c = iterator.next()

			iterator.reset()
			def d = iterator.next()
		then:
			iterator instanceof Iterators.Resettable

			a == "first"
			b == "second"
			c == "third"

			d == "first"

			notThrown NoSuchElementException
	}

	def "from(int, int, T,,,) returns valid, resettable iterator starting from and ending at provided index"() {
		when:
			def iterator = Iterators.from(1, 2, "first", "second", "third", "fourth")

			def a = iterator.next()
			def b = iterator.next()

			iterator.next()
		then:
			iterator instanceof Iterators.Resettable

			a == "second"
			b == "third"

			thrown(NoSuchElementException)

			iterator.reset()
			iterator.next() == "second"
	}

	def "from(T, UnaryOperator) returns valid, resettable iterator"() {
		given:
			def random = new Random()
			def seed = "abcdefghijklmnopqrstuvwxyz"
		when:
			def iterator = Iterators.from(seed, {
				Strings.shuffle(random, it)
			})
			def a = iterator.next()
			def b = iterator.next()
		then:
			iterator instanceof Iterators.Resettable
			notThrown NoSuchElementException

			a == seed
			b != seed

			iterator.reset()
			iterator.next() == seed
	}
}