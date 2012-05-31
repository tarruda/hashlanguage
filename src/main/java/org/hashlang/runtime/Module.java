package org.hashlang.runtime;

import org.hashlang.runtime.functions.BuiltinFunction;

public interface Module extends Context {

	void installBuiltin(BuiltinFunction f);

}
