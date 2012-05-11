package hash.runtime.mixins;

import hash.lang.Hash;
import hash.runtime.functions.Method;

public class Mixin extends Hash {

	protected void installMethod(Method method) {
		put(method.getName(), method);
	}
}
