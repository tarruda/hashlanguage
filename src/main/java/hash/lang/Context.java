package hash.lang;

import hash.runtime.functions.BuiltinFunction;

import java.util.Map;

public interface Context extends Map {
	Context getParent();
	
	void installBuiltin(BuiltinFunction f);
}
