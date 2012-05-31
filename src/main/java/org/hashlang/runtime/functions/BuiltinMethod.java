package org.hashlang.runtime.functions;

public abstract class BuiltinMethod extends BuiltinFunction {

	protected String name;

	public BuiltinMethod(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return String.format("Builtin method '%s'", name);
	}
}
