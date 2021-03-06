package org.hashlang.runtime.functions;


public abstract class UnaryOperator extends BuiltinFunction {

	public static String getSlotName(String operator) {
		return operator + "#";
	}

	private String op;

	public UnaryOperator(String op) {
		this.op = op;
	}
	
	@Override
	public String getName() {
		return getSlotName(op);
	}

	@Override
	public String toString() {
		return String.format("Unary operator '%s'", op);
	}
}
