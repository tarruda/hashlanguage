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

	public static IllegalArgumentException illegalArgCount() {
		return new IllegalArgumentException(
				"Function invoked with a wrong number of arguments");
	}

	public static IllegalArgumentException illegalArg(String fName,
			String expectedType) {
		return illegalArg(fName, expectedType, 0);
	}

	public static IllegalArgumentException illegalArg(String fName,
			String expectedType, int argIdx) {
		return illegalArg(String.format(
				"Function '%s' expecting argument %s of type '%s'", fName,
				argIdx, expectedType));
	}

	public static IllegalArgumentException illegalArg(String msg) {
		return new IllegalArgumentException(msg);
	}

	public static IndexOutOfBoundsException outOfBounds() {
		return new IndexOutOfBoundsException();
	}
}
