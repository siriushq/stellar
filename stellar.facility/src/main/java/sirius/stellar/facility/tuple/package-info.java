/**
 * Package containing various tuple structures (anonymous object structures).
 * <p>
 * Subclass implementations may be mutable or immutable, but there is no constraint
 * on the type of the stored objects. This means that if a mutable object is stored
 * inside the tuple, the tuple itself effectively becomes mutable.
 * <p>
 * There is no implementation of a tuple containing only one element (usually called
 * a "unit"), as an {@link java.util.Optional} can be used for that purpose instead.
 *
 * @see sirius.stellar.facility.tuple.Couple Couple, holding two elements.
 * @see sirius.stellar.facility.tuple.Triplet Triplet, holding three elements.
 * @see sirius.stellar.facility.tuple.Quartet Quartet, holding four elements.
 * @see sirius.stellar.facility.tuple.Quintet Quintet, holding five elements.
 * @see sirius.stellar.facility.tuple.Sextet Sextet, holding six elements.
 * @see sirius.stellar.facility.tuple.Septet Septet, holding seven elements.
 * @see sirius.stellar.facility.tuple.Octet Octet, holding eight elements.
 *
 * @see sirius.stellar.facility
 * @since 1u1
 */
package sirius.stellar.facility.tuple;