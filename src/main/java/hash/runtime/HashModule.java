package hash.runtime;

import hash.runtime.functions.BuiltinFunction;
import hash.util.Err;

@SuppressWarnings("serial")
public class HashModule extends HashContext implements Module {

	public void installBuiltin(BuiltinFunction f) {
		put(f.getName(), f);
	}

	@Override
	public Object get(Object key) {
		if (!containsKey(key))
			throw Err.nameNotDefined(key);
		return super.get(key);
	}

}
