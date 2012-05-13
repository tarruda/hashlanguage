package hash.runtime.mixins;

import hash.runtime.functions.BuiltinFunction;

import java.util.HashMap;

@SuppressWarnings("serial")
public class Mixin extends HashMap<Object, Object> {

	protected void installMethod(BuiltinFunction method) {
		put(method.getName(), method);
	}
}
