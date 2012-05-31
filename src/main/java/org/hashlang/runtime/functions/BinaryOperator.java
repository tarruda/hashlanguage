package org.hashlang.runtime.functions;


public abstract class BinaryOperator extends BuiltinFunction {

	public static String getSlotName(String operator) {
		return operator + "##";
	}

	protected String op;

	public BinaryOperator(String op) {
		this.op = op;		
	}

	@Override
	public String getName() {
		return getSlotName(op);
	}

	@Override
	public String toString() {
		return String.format("Binary operator '%s'", op);
	}
}
