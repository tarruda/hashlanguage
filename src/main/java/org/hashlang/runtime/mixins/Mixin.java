package org.hashlang.runtime.mixins;


import java.util.HashMap;

import org.hashlang.runtime.AppRuntime;
import org.hashlang.runtime.functions.BuiltinFunction;

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
