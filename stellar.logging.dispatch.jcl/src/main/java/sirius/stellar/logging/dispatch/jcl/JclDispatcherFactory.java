package sirius.stellar.logging.dispatch.jcl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/// Implementation of [org.apache.commons.logging.LogFactory] used for obtaining instances of [JclDispatcher].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class JclDispatcherFactory extends org.apache.commons.logging.LogFactory {

	private static final JclDispatcherFactory instance = new JclDispatcherFactory();

	public static final String LOG_PROPERTY = "org.apache.commons.logging.Log";

	private final ConcurrentMap<String, org.apache.commons.logging.Log> loggers = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<>();

	public static JclDispatcherFactory getInstance() {
		return instance;
	}

	@Override
	public Object getAttribute(String name) {
		return this.attributes.get(name);
	}

	@Override
	public String[] getAttributeNames() {
        return (String[]) this.attributes.keySet().stream()
				.map(String::valueOf)
				.toArray();
	}

	@Override
	public org.apache.commons.logging.Log getInstance(Class clazz) throws org.apache.commons.logging.LogConfigurationException {
		return getInstance(clazz.getName());
	}

	@Override
	public org.apache.commons.logging.Log getInstance(String name) throws org.apache.commons.logging.LogConfigurationException {
		return loggers.computeIfAbsent(name, JclDispatcher::new);
	}

	@Override
	public void release() {
		throw new IllegalStateException();
	}

	@Override
	public void removeAttribute(String name) {
		this.attributes.remove(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		this.attributes.put(name, value);
	}
}