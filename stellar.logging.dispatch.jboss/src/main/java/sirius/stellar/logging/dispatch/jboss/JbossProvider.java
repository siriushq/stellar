package sirius.stellar.logging.dispatch.jboss;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/// Implementation of [org.jboss.logging.LoggerProvider] used for obtaining instances of [JbossDispatcher].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class JbossProvider implements org.jboss.logging.LoggerProvider {

	private final Map<String, Object> mdc = new ConcurrentHashMap<>();
	private final Deque<JbossNdcEntry> ndc = new ConcurrentLinkedDeque<>();

	@Override
	public org.jboss.logging.Logger getLogger(String name) {
		return new JbossDispatcher(name);
	}

	@Override
	public void clearMdc() {
		this.mdc.clear();
	}

	@Override
	public Object putMdc(String key, Object value) {
		return this.mdc.put(key, value);
	}

	@Override
	public Object getMdc(String key) {
		return this.mdc.get(key);
	}

	@Override
	public void removeMdc(String key) {
		this.mdc.remove(key);
	}

	@Override
	public Map<String, Object> getMdcMap() {
		return this.mdc;
	}

	@Override
	public void clearNdc() {
		this.ndc.clear();
	}

	@Override
	public String getNdc() {
		if (this.ndc.isEmpty()) return null;
		return this.ndc.peek().merged;
	}

	@Override
	public int getNdcDepth() {
		return this.ndc.size();
	}

	@Override
	public String popNdc() {
		if (this.ndc.isEmpty()) return "";
		return this.ndc.pop().current;
	}

	@Override
	public String peekNdc() {
		if (this.ndc.isEmpty()) return "";
		return this.ndc.peek().current;
	}

	@Override
	public void pushNdc(String message) {
		this.ndc.push(this.ndc.isEmpty() ? new JbossNdcEntry(message) : new JbossNdcEntry(this.ndc.peek(), message));
	}

	@Override
	public void setNdcMaxDepth(int ndcMaxDepth) {
		while (this.ndc.size() > ndcMaxDepth) this.ndc.pop();
	}
}