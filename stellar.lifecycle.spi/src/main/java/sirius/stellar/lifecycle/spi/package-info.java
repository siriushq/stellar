@GenerateAPContext
@GenerateModuleInfoReader
@GeneratePrisms({
		@GeneratePrism(name = "ServicePrism", value = Service.class),
		@GeneratePrism(name = "ServiceProviderPrism", value = Service.Provider.class)
})
package sirius.stellar.lifecycle.spi;

import io.avaje.prism.GenerateAPContext;
import io.avaje.prism.GenerateModuleInfoReader;
import io.avaje.prism.GeneratePrism;
import io.avaje.prism.GeneratePrisms;