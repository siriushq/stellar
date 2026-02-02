package sirius.stellar.configuration.sighup;

import org.junit.jupiter.api.condition.EnabledForJreRange;

import static org.junit.jupiter.api.condition.JRE.JAVA_22;
import static org.junit.jupiter.api.condition.JRE.JAVA_23;

final class SignalConfigurationReloaderTest {

	@EnabledForJreRange(max = JAVA_22)
	void sun() {
	// TODO
	}

	@EnabledForJreRange(min = JAVA_23)
	void ffm() {
	// TODO
	}
}