package sirius.stellar.platform;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;

import java.lang.management.*;
import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.random.RandomGenerator;

import static java.lang.management.ManagementFactory.*;

/**
 * Factory used for wiring most beans provided in the
 * inversion-of-control container provided by {@link Platform}.
 *
 * @author Mahied Maruf (mechite)
 * @since 1.0
 */
@Factory
public final class PlatformFactory {

	@Bean
	Runtime runtime() {
		return Runtime.getRuntime();
	}

	@Bean
	ExecutorService executorService() {
		return Executors.newVirtualThreadPerTaskExecutor();
	}

	@Bean
	RandomGenerator randomGenerator() {
		return new SecureRandom();
	}

	@Bean
	ClassLoadingMXBean classLoadingMXBean() {
		return getClassLoadingMXBean();
	}

	@Bean
	CompilationMXBean compilationMXBean() {
		return getCompilationMXBean();
	}

	@Bean
	List<GarbageCollectorMXBean> garbageCollectorMXBeans() {
		return getGarbageCollectorMXBeans();
	}

	@Bean
	MemoryMXBean memoryMXBean() {
		return getMemoryMXBean();
	}

	@Bean
	List<MemoryManagerMXBean> memoryManagerMXBeans() {
		return getMemoryManagerMXBeans();
	}

	@Bean
	List<MemoryPoolMXBean> memoryPoolMXBeans() {
		return getMemoryPoolMXBeans();
	}

	@Bean
	OperatingSystemMXBean operatingSystemMXBean() {
		return getOperatingSystemMXBean();
	}

	@Bean
	RuntimeMXBean runtimeMXBean() {
		return getRuntimeMXBean();
	}

	@Bean
	ThreadMXBean threadMXBean() {
		return getThreadMXBean();
	}
}