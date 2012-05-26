package hash.runtime;

import hash.runtime.functions.BuiltinFunction;

import java.util.Map;

public interface Context extends Map {
	Context getParent();
	
	void installBuiltin(BuiltinFunction f);
	
	Object restore();
	
	void save(Object value);
}
