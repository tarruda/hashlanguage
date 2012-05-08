package hash.runtime.exceptions;

public class HashException extends RuntimeException {
	private static final long serialVersionUID = 4083410581591582058L;

	public HashException() {
	}

	public HashException(String msg) {
		super(msg);
	}

	public HashException(Throwable cause) {
		super(cause);
	}

	public HashException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	@Override
	public StackTraceElement[] getStackTrace() {
		// TODO Override default behavior in order to get decent stack traces
		return super.getStackTrace();
	}
}
