package hash.runtime.util;

import hash.runtime.exceptions.HashException;

public class Err {
	public static RuntimeException binaryOperatorNotImplemented(
			String operator, Object arg1, Object arg2) {
		throw new HashException(
				String.format(
						"Operator '%s' is not implemented for objects of type '%s' and '%s'",
						operator, Type.nameFor(arg1), Type.nameFor(arg2)));
	}
	
	
}
