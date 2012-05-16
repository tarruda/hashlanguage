package hash.parsing.exceptions;

@SuppressWarnings("serial")
public class TreeValidationException extends ParsingException {

	private int line;
	private int charPositionInLine;

	public TreeValidationException(int line, int charPositionInLine, String msg) {
		super(msg);
		this.line = line;
		this.charPositionInLine = charPositionInLine;
	}

	@Override
	public int getLine() {
		return line;
	}

	@Override
	public int getCharPositionInLine() {
		return charPositionInLine;
	}
}
