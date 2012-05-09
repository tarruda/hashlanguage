package hash.runtime.mixins;

import hash.lang.Hash;
import hash.runtime.functions.BuiltinMethod;

public class Mixin extends Hash {

	protected void installMethod(BuiltinMethod method) {
		put(method.getName(), method);
	}
}
