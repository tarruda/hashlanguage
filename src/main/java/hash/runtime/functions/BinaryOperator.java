package hash.runtime.functions;


public abstract class BinaryOperator extends BuiltinMethod {

	public static String getSlotName(String operator) {
		return operator + "##";
	}

	private String op;

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
