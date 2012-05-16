package hash.parsing.exceptions;

import org.antlr.runtime.RecognitionException;

@SuppressWarnings("serial")
public class ParsingException extends RuntimeException {

	public ParsingException() {
	}

	public ParsingException(String msg) {
		super(msg);
	}

	public ParsingException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public ParsingException(Throwable cause) {
		super(cause);
	}

	public int getLine() {
		return ((RecognitionException) getCause()).line;
	}

	public int getCharPositionInLine() {
		return ((RecognitionException) getCause()).charPositionInLine;
	}
}
