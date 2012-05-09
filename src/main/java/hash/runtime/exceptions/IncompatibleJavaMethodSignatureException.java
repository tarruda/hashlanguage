package hash.runtime.exceptions;

public class IncompatibleJavaMethodSignatureException extends HashException {

	private static final long serialVersionUID = -3054180511925173977L;

	public IncompatibleJavaMethodSignatureException() {
		super("Arguments passed are invalid for this java method");
	}
}
