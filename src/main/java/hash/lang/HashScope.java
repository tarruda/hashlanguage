package hash.lang;

import hash.util.Err;

import java.util.HashMap;

@SuppressWarnings("serial")
public class HashScope extends HashMap implements Scope {

	private Scope parent;

	public HashScope() {
	}

	public HashScope(Scope parent) {
		this.parent = parent;
	}

	public Scope getParent() {
		return parent;
	}

	@Override
	public Object get(Object key) {
		if (!containsKey(key))
			if (parent != null)
				return parent.get(key);
			else
				throw Err.nameNotDefined(key);
		return super.get(key);
	}
}
