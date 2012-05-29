package hash.runtime.mixins;

import hash.runtime.AppRuntime;
import hash.runtime.functions.BuiltinFunction;

import java.util.HashMap;

@SuppressWarnings("serial")
public class Mixin extends HashMap<Object, Object> {

	protected final AppRuntime runtime;

	public Mixin(AppRuntime runtime){
		this.runtime = runtime;
	}
	
	protected void installMethod(BuiltinFunction method) {
		put(method.getName(), method);
	}
}
