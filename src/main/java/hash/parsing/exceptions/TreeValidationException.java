package hash.parsing.exceptions;

public class TreeValidationException extends RuntimeException {
	private static final long serialVersionUID = 4891033425008881335L;
	private int line;
	private int charPositionInLine;

	public TreeValidationException(int line, int charPositionInLine, String msg) {
		super(msg);
		this.line = line;
		this.charPositionInLine = charPositionInLine;
	}

	public int getLine() {
		return line;
	}

	public int getCharPositionInLine() {
		return charPositionInLine;
	}
}
