package hash.runtime.exceptions;

public class IllegalArgTypeException extends HashException {

	private static final long serialVersionUID = 6924817829288944056L;

	public IllegalArgTypeException(String fName, String expectedType) {
		super(String.format("Function '%s' expecting argument of type '%s'",
				fName, expectedType));
	}
}
