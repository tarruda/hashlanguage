package hash.runtime;

import hash.runtime.functions.BuiltinFunction;

public interface Module extends Context {

	void installBuiltin(BuiltinFunction f);

}
