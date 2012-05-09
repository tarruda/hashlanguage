package hash.util;

import hash.runtime.exceptions.HashException;
import hash.runtime.exceptions.InvalidReturnValueException;

public class Err {
	public static HashException binaryOperatorNotImplemented(String operator,
			Object arg1, Object arg2) {
		return new HashException(
				String.format(
						"Operator '%s' is not implemented for objects of type '%s' and '%s'",
						operator, Types.nameFor(arg1), Types.nameFor(arg2)));
	}

	public static InvalidReturnValueException invalidComparisonResult() {
		return new InvalidReturnValueException(
				"Comparison must return a number");
	}

}
