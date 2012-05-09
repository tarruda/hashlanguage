package hash.lang;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Base class for all objects defined in the Hash language. *
 * 
 * @author Thiago de Arruda *
 */
public class Hash implements Map<Object, Object> {

	// For now this class will delegate calls to an internal HashMap.
	// TODO Find an implementation better suited for this application.
	private final HashMap<Object, Object> impl;

	public Hash() {
		this.impl = new HashMap<Object, Object>();
	}

	public int size() {
		return impl.size();
	}

	public boolean isEmpty() {
		return impl.isEmpty();
	}

	public boolean containsKey(Object key) {
		return impl.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return impl.containsValue(value);
	}

	public Object get(Object key) {
		return impl.get(key);
	}

	public Object put(Object key, Object value) {
		return impl.put(key, value);
	}

	public Object remove(Object key) {
		return impl.remove(key);
	}

	public void putAll(Map<? extends Object, ? extends Object> m) {
		impl.putAll(m);
	}

	public void clear() {
		impl.clear();
	}

	public Set<Object> keySet() {
		return impl.keySet();
	}

	public Collection<Object> values() {
		return impl.values();
	}

	public Set<java.util.Map.Entry<Object, Object>> entrySet() {
		return impl.entrySet();
	}

	@Override
	public String toString() {
		return impl.toString();
	}
}
