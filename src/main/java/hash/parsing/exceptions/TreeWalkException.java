package hash.parsing.exceptions;

public class TreeWalkException extends RuntimeException {

	private static final long serialVersionUID = 4891033425008881335L;

	public TreeWalkException() {
	}

	public TreeWalkException(String msg) {
		super(msg);
	}

	public TreeWalkException(Throwable cause) {
		super(cause);
	}
}
