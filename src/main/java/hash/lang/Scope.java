package hash.lang;

import hash.runtime.functions.BuiltinFunction;

import java.util.Map;

public interface Scope extends Map {
	Scope getParent();
	
	void installBuiltin(BuiltinFunction f);
}
