package hash.runtime.mixins;

import hash.runtime.functions.Method;

import java.util.HashMap;

@SuppressWarnings("serial")
public class Mixin extends HashMap<Object, Object> {

	protected void installMethod(Method method) {
		put(method.getName(), method);
	}
}
