package hash.util;

import hash.runtime.exceptions.HashException;
import hash.runtime.exceptions.InvalidReturnValueException;

public class Err {
	public static HashException binaryOperatorNotImplemented(String operator,
			Object arg1, Object arg2) {
		return new HashException(
				String.format(
						"Binary operator '%s' is not implemented for types '%s' and '%s'",
						operator, arg1.getClass().getName(), arg2.getClass()
								.getName()));
	}

	public static HashException unaryOperatorNotImplemented(String operator,
			Object arg1) {
		return new HashException(String.format(
				"Unary operator '%s' is not implemented for type '%s'",
				operator, arg1.getClass().getName()));
	}

	public static InvalidReturnValueException invalidComparisonResult() {
		return new InvalidReturnValueException(
				"Comparison must return a number");
	}
}
