package sirius.stellar.logging.dispatch.jboss;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static java.lang.ThreadLocal.withInitial;

/// Implementation of [org.jboss.logging.LoggerProvider] used for obtaining instances of [JbossDispatcher].
///
/// @author Mahied Maruf (mechite)
/// @since 1.0
public final class JbossProvider implements org.jboss.logging.LoggerProvider {

	private final ThreadLocal<Map<String, Object>> mdc = withInitial(HashMap::new);
	private final ThreadLocal<Deque<JbossNdcEntry>> ndc = withInitial(ArrayDeque::new);

	@Override
	public org.jboss.logging.Logger getLogger(String name) {
		return new JbossDispatcher(name);
	}

	@Override
	public void clearMdc() {
		this.mdc.remove();
	}

	@Override
	public Object putMdc(String key, Object value) {
		return this.mdc.get().put(key, value);
	}

	@Override
	public Object getMdc(String key) {
		return this.mdc.get().get(key);
	}

	@Override
	public void removeMdc(String key) {
		this.mdc.get().remove(key);
	}

	@Override
	public Map<String, Object> getMdcMap() {
		return this.mdc.get();
	}

	@Override
	public void clearNdc() {
		this.ndc.remove();
	}

	@Override
	public String getNdc() {
		if (this.ndc.get().isEmpty()) return null;
		return this.ndc.get().peek().merged;
	}

	@Override
	public int getNdcDepth() {
		return this.ndc.get().size();
	}

	@Override
	public String popNdc() {
		if (this.ndc.get().isEmpty()) return "";
		return this.ndc.get().pop().current;
	}

	@Override
	public String peekNdc() {
		if (this.ndc.get().isEmpty()) return "";
		return this.ndc.get().peek().current;
	}

	@Override
	public void pushNdc(String message) {
		JbossNdcEntry entry = this.ndc.get().isEmpty() ?
			new JbossNdcEntry(message) :
			new JbossNdcEntry(this.ndc.get().peek(), message);
		this.ndc.get().push(entry);
	}

	@Override
	public void setNdcMaxDepth(int ndcMaxDepth) {
		while (this.ndc.get().size() > ndcMaxDepth) this.ndc.get().pop();
	}
}