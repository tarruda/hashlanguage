package hash.lang;

import hash.runtime.functions.BuiltinFunction;
import hash.util.Err;

import java.util.HashMap;

@SuppressWarnings("serial")
public class HashContext extends HashMap implements Context {

	private Context parent;

	public HashContext() {
		
	}

	public HashContext(Context parent) {
		this.parent = parent;
	}

	public Context getParent() {
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

	public void installBuiltin(BuiltinFunction f) {
		put(f.getName(), f);
	}
}
