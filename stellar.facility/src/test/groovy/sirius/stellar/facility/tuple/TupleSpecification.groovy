package sirius.stellar.facility.tuple

import sirius.stellar.facility.exception.ImmutableModificationException
import spock.lang.Specification

import static sirius.stellar.facility.tuple.Couple.*
import static sirius.stellar.facility.tuple.Triplet.*
import static sirius.stellar.facility.tuple.Quartet.*
import static sirius.stellar.facility.tuple.Quintet.*
import static sirius.stellar.facility.tuple.Sextet.*
import static sirius.stellar.facility.tuple.Septet.*
import static sirius.stellar.facility.tuple.Octet.*

class TupleSpecification extends Specification {

	//#region Couple
	def "Couple - immutableCouple returns immutable couple which throws ImmutableModificationException on modification"() {
		given:
			def couple = immutableCouple("a", "b")
		when:
			couple.first("c")
		then:
			thrown(ImmutableModificationException)
	}

	def "Couple - mutableCouple returns mutable couple, which doesn't throw ImmutableModificationException"() {
		given:
			def couple = mutableCouple("a", "b")
		when:
			couple.first("c")
		then:
			couple.first() == "c"
			notThrown ImmutableModificationException
	}

	def "Couple - factory methods all return instances which correctly return values"() {
		given:
			def immutableCouple = immutableCouple("a", "b")
			def mutableCouple = mutableCouple("c", "d")
		when:
			def a = immutableCouple.first()
			def b = immutableCouple.second()

			def c = mutableCouple.first()
			def d = mutableCouple.second()
		then:
			a == "a"
			b == "b"

			c == "c"
			d == "d"
	}

	def "Couple - instances correctly implement Map,Entry"() {
		given:
			def immutableCouple = immutableCouple("a", "b")
			def mutableCouple = mutableCouple("c", "d")
		when:
			def a = immutableCouple.getKey()
			def b = immutableCouple.getValue()
			def c = mutableCouple.getKey()
			def d = mutableCouple.getValue()
		then:
			a == "a"
			b == "b"
			c == "c"
			d == "d"
	}
	//#endregion

	//#region Triplet
	def "Triplet - immutableTriplet returns immutable triplet which throws ImmutableModificationException on modification"() {
		given:
			def triplet = immutableTriplet("a", "b", "c")
		when:
			triplet.first("d")
		then:
			thrown(ImmutableModificationException)
	}

	def "Triplet - mutableTriplet returns mutable triplet, which doesn't throw ImmutableModificationException"() {
		given:
			def triplet = mutableTriplet("a", "b", "c")
		when:
			triplet.first("d")
		then:
			triplet.first() == "d"
			notThrown ImmutableModificationException
	}

	def "Triplet - factory methods all return instances which correctly return values"() {
		given:
			def immutableTriplet = immutableTriplet("a", "b", "c")
			def mutableTriplet = mutableTriplet("d", "e", "f")
		when:
			def a = immutableTriplet.first()
			def b = immutableTriplet.second()
			def c = immutableTriplet.third()

			def d = mutableTriplet.first()
			def e = mutableTriplet.second()
			def f = mutableTriplet.third()
		then:
			a == "a"
			b == "b"
			c == "c"

			d == "d"
			e == "e"
			f == "f"
	}
	//#endregion

	//#region Quartet
	def "Quartet - immutableQuartet returns immutable quartet which throws ImmutableModificationException on modification"() {
		given:
			def quartet = immutableQuartet("a", "b", "c", "d")
		when:
			quartet.first("e")
		then:
			thrown(ImmutableModificationException)
	}

	def "Quartet - mutableQuartet returns mutable quartet, which doesn't throw ImmutableModificationException"() {
		given:
			def quartet = mutableQuartet("a", "b", "c", "d")
		when:
			quartet.first("e")
		then:
			quartet.first() == "e"
			notThrown ImmutableModificationException
	}

	def "Quartet - factory methods all return instances which correctly return values"() {
		given:
			def immutableQuartet = immutableQuartet("a", "b", "c", "d")
			def mutableQuartet = mutableQuartet("e", "f", "g", "h")
		when:
			def a = immutableQuartet.first()
			def b = immutableQuartet.second()
			def c = immutableQuartet.third()
			def d = immutableQuartet.fourth()

			def e = mutableQuartet.first()
			def f = mutableQuartet.second()
			def g = mutableQuartet.third()
			def h = mutableQuartet.fourth()
		then:
			a == "a"
			b == "b"
			c == "c"
			d == "d"

			e == "e"
			f == "f"
			g == "g"
			h == "h"
	}
	//#endregion

	//#region Quintet
	def "Quintet - immutableQuintet returns immutable quintet which throws ImmutableModificationException on modification"() {
		given:
			def quintet = immutableQuintet("a", "b", "c", "d", "e")
		when:
			quintet.first("f")
		then:
			thrown(ImmutableModificationException)
	}

	def "Quintet - mutableQuintet returns mutable quintet, which doesn't throw ImmutableModificationException"() {
		given:
			def quintet = mutableQuintet("a", "b", "c", "d", "e")
		when:
			quintet.first("f")
		then:
			quintet.first() == "f"
			notThrown ImmutableModificationException
	}

	def "Quintet - factory methods all return instances which correctly return values"() {
		given:
			def immutableQuintet = immutableQuintet("a", "b", "c", "d", "e")
			def mutableQuintet = mutableQuintet("f", "g", "h", "i", "j")
		when:
			def a = immutableQuintet.first()
			def b = immutableQuintet.second()
			def c = immutableQuintet.third()
			def d = immutableQuintet.fourth()
			def e = immutableQuintet.fifth()

			def f = mutableQuintet.first()
			def g = mutableQuintet.second()
			def h = mutableQuintet.third()
			def i = mutableQuintet.fourth()
			def j = mutableQuintet.fifth()
		then:
			a == "a"
			b == "b"
			c == "c"
			d == "d"
			e == "e"

			f == "f"
			g == "g"
			h == "h"
			i == "i"
			j == "j"
	}
	//#endregion

	//#region Sextet
	def "Sextet - immutableSextet returns immutable sextet which throws ImmutableModificationException on modification"() {
		given:
			def sextet = immutableSextet("a", "b", "c", "d", "e", "f")
		when:
			sextet.first("g")
		then:
			thrown(ImmutableModificationException)
	}

	def "Sextet - mutableSextet returns mutable sextet, which doesn't throw ImmutableModificationException"() {
		given:
			def sextet = mutableSextet("a", "b", "c", "d", "e", "f")
		when:
			sextet.first("g")
		then:
			sextet.first() == "g"
			notThrown ImmutableModificationException
	}

	def "Sextet - factory methods all return instances which correctly return values"() {
		given:
			def immutableSextet = immutableSextet("a", "b", "c", "d", "e", "f")
			def mutableSextet = mutableSextet("g", "h", "i", "j", "k", "l")
		when:
			def a = immutableSextet.first()
			def b = immutableSextet.second()
			def c = immutableSextet.third()
			def d = immutableSextet.fourth()
			def e = immutableSextet.fifth()
			def f = immutableSextet.sixth()

			def g = mutableSextet.first()
			def h = mutableSextet.second()
			def i = mutableSextet.third()
			def j = mutableSextet.fourth()
			def k = mutableSextet.fifth()
			def l = mutableSextet.sixth()
		then:
			a == "a"
			b == "b"
			c == "c"
			d == "d"
			e == "e"
			f == "f"

			g == "g"
			h == "h"
			i == "i"
			j == "j"
			k == "k"
			l == "l"
	}
	//#endregion

	//#region Septet
	def "Septet - immutableSeptet returns immutable septet which throws ImmutableModificationException on modification"() {
		given:
			def septet = immutableSeptet("a", "b", "c", "d", "e", "f", "g")
		when:
			septet.first("h")
		then:
			thrown(ImmutableModificationException)
	}

	def "Septet - mutableSeptet returns mutable septet, which doesn't throw ImmutableModificationException"() {
		given:
			def septet = mutableSeptet("a", "b", "c", "d", "e", "f", "g")
		when:
			septet.first("h")
		then:
			septet.first() == "h"
			notThrown ImmutableModificationException
	}

	def "Septet - factory methods all return instances which correctly return values"() {
		given:
			def immutableSeptet = immutableSeptet("a", "b", "c", "d", "e", "f", "g")
			def mutableSeptet = mutableSeptet("h", "i", "j", "k", "l", "m", "n")
		when:
			def a = immutableSeptet.first()
			def b = immutableSeptet.second()
			def c = immutableSeptet.third()
			def d = immutableSeptet.fourth()
			def e = immutableSeptet.fifth()
			def f = immutableSeptet.sixth()
			def g = immutableSeptet.seventh()

			def h = mutableSeptet.first()
			def i = mutableSeptet.second()
			def j = mutableSeptet.third()
			def k = mutableSeptet.fourth()
			def l = mutableSeptet.fifth()
			def m = mutableSeptet.sixth()
			def n = mutableSeptet.seventh()
		then:
			a == "a"
			b == "b"
			c == "c"
			d == "d"
			e == "e"
			f == "f"
			g == "g"

			h == "h"
			i == "i"
			j == "j"
			k == "k"
			l == "l"
			m == "m"
			n == "n"
	}
	//#endregion

	//#region Octet
	def "Octet - immutableOctet returns immutable octet which throws ImmutableModificationException on modification"() {
		given:
			def octet = immutableOctet("a", "b", "c", "d", "e", "f", "g", "h")
		when:
			octet.first("i")
		then:
			thrown(ImmutableModificationException)
	}

	def "Octet - mutableOctet returns mutable octet, which doesn't throw ImmutableModificationException"() {
		given:
			def octet = mutableOctet("a", "b", "c", "d", "e", "f", "g", "h")
		when:
			octet.first("i")
		then:
			octet.first() == "i"
			notThrown ImmutableModificationException
	}

	def "Octet - factory methods all return instances which correctly return values"() {
		given:
			def immutableOctet = immutableOctet("a", "b", "c", "d", "e", "f", "g", "h")
			def mutableOctet = mutableOctet("i", "j", "k", "l", "m", "n", "o", "p")
		when:
			def a = immutableOctet.first()
			def b = immutableOctet.second()
			def c = immutableOctet.third()
			def d = immutableOctet.fourth()
			def e = immutableOctet.fifth()
			def f = immutableOctet.sixth()
			def g = immutableOctet.seventh()
			def h = immutableOctet.eighth()

			def i = mutableOctet.first()
			def j = mutableOctet.second()
			def k = mutableOctet.third()
			def l = mutableOctet.fourth()
			def m = mutableOctet.fifth()
			def n = mutableOctet.sixth()
			def o = mutableOctet.seventh()
			def p = mutableOctet.eighth()
		then:
			a == "a"
			b == "b"
			c == "c"
			d == "d"
			e == "e"
			f == "f"
			g == "g"
			h == "h"

			i == "i"
			j == "j"
			k == "k"
			l == "l"
			m == "m"
			n == "n"
			o == "o"
			p == "p"
	}
	//#endregion
}