package hash.runtime.exceptions;

public class IllegalArgCountException extends HashException {

	private static final long serialVersionUID = -7279853122361757870L;

	public IllegalArgCountException() {
		super("Function invoked with a wrong number of arguments");
	}
}
