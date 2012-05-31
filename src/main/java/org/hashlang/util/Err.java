package org.hashlang.util;

public class Err {

	public static RuntimeException ex(Throwable cause) {
		return new RuntimeException(cause);
	}

	public static RuntimeException ex(String msg) {
		return new RuntimeException(msg);
	}

	public static RuntimeException ex(String msg, Throwable cause) {
		return new RuntimeException(msg, cause);
	}

	public static RuntimeException importError(String importName,
			Throwable cause) {
		return new RuntimeException(String.format("Cannot import '%s'",
				importName), cause);
	}

	public static RuntimeException binaryOperatorNotImplemented(
			String operator, Object leftOperand, Object rightOperand) {
		return new RuntimeException(
				String.format(
						"Binary operator '%s' is not implemented for types '%s' and '%s'",
						operator, leftOperand.getClass().getName(),
						rightOperand.getClass().getName()));
	}

	public static RuntimeException unaryOperatorNotImplemented(String operator,
			Object operand) {
		return new RuntimeException(String.format(
				"Unary operator '%s' is not implemented for type '%s'",
				operator, operand.getClass().getName()));
	}

	public static RuntimeException invalidComparisonResult() {
		return new RuntimeException("Comparison must return a number");
	}

	public static RuntimeException illegalJavaArgs() {
		return new IllegalArgumentException(
				"Arguments passed are invalid for this java method");
	}

	public static RuntimeException illegalArgCount() {
		return new IllegalArgumentException(
				"Function invoked with a wrong number of arguments");
	}

	public static RuntimeException illegalArg(String fName, String expectedType) {
		return illegalArg(fName, expectedType, 0);
	}

	public static RuntimeException illegalArg(String fName,
			String expectedType, int argIdx) {
		return illegalArg(String.format(
				"Function '%s' expecting argument %s of type '%s'", fName,
				argIdx, expectedType));
	}

	public static RuntimeException nullIndex() {
		return new IllegalArgumentException("Index argument is a null referece");
	}

	public static RuntimeException illegalArg(String msg) {
		return new IllegalArgumentException(msg);
	}

	public static RuntimeException illegalReturnVal() {
		return new IllegalArgumentException(
				"Function returned an invalid value");
	}

	public static RuntimeException outOfBounds() {
		return new IndexOutOfBoundsException();
	}

	public static RuntimeException nameNotDefined(Object name) {
		return new RuntimeException(
				String.format(
						"Name '%s' is not defined in the current or any enclosing context",
						name));
	}

	public static RuntimeException attributeNotDefined(Object name) {
		return new RuntimeException(String.format(
				"Attribute '%s' is not defined", name));
	}

	public static RuntimeException attributeNotFunction(Object name) {
		return new RuntimeException(String.format(
				"Attribute '%s' is not a function", name));
	}

	public static RuntimeException functionIsMethod() {
		return new RuntimeException(
				"Function is a method and must be invoked as such");
	}

	public static RuntimeException notImplemented() {
		return new UnsupportedOperationException();
	}

	public static IllegalStateException illegalState(String string) {
		return new IllegalStateException(string);
	}
}
