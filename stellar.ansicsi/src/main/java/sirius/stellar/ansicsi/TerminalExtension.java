package sirius.stellar.ansicsi;

/// SPI (Service Provider Interface) for any provider that aims to add
/// behavioral extensions that are wired automatically inside the static
/// initialization block of [Terminal].
///
/// @since 1.0
public interface TerminalExtension {

	/// Run automatically when this provider is discovered.
	/// @since 1.0
	void wire() throws Throwable;
}