package sirius.stellar.configuration.inject;

import io.avaje.inject.spi.ConfigPropertyPlugin;
import sirius.stellar.lifecycle.spi.Service;

import java.util.Optional;

import static sirius.stellar.configuration.Configuration.*;

/// Implementation of `avaje-inject` [ConfigPropertyPlugin], provided when the optional dependency
/// is available on the module path.
@Service.Provider
public class DConfigPropertyPlugin implements ConfigPropertyPlugin {

	@Override
	public Optional<String> get(String property) {
		return propertyOptional(property);
	}

	@Override
	public boolean contains(String property) {
		return configurationMap().containsKey(property);
	}

	@Override
	public boolean equalTo(String property, String value) {
		return propertyOptional(property)
				.orElse("false")
				.equals(value);
	}
}