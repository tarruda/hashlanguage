package hash.runtime.exceptions;

public class InvalidReturnValueException extends HashException {

	private static final long serialVersionUID = -6953545415419448713L;

	public InvalidReturnValueException() {
		super("Function returned an invalid value");
	}
	
	public InvalidReturnValueException(String msg) {
		super(msg);
	}
}
