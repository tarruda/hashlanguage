package hash.parsing;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

@SuppressWarnings("serial")
public abstract class AbstractHashLexer extends Lexer {
	private static final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "abcdefghijklmnopqrstuvwxyz" + "_$";
	private static final String digits = "0123456789";
	private static final String whiteSpaces = " \n\t\r";

	public AbstractHashLexer() {
	}

	public AbstractHashLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}

	public AbstractHashLexer(CharStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected int convertFromHexDigits(String c1, String c2, String c3,
			String c4) {
		String combined = c1 + c2 + c3 + c4;
		return Integer.parseInt(combined, 16);
	}

	protected void validateInteger(int radix, String text)
			throws RecognitionException {
		try {
			long l = Long.parseLong(text, radix);
			setText(Long.toHexString(l));
		} catch (NumberFormatException e) {
			RecognitionException ex = new RecognitionException() {
				@Override
				public String getMessage() {
					return "Integer literal outside maximum range(64 bit)";
				}
			};
			ex.line = input.getLine();
			ex.charPositionInLine = input.getCharPositionInLine();
			throw ex;
		}
	}

	protected boolean isNumberAttributeAccess() {
		// try to match a number following a dot following an identifier.
		// if we fail to match, then we stop and return false immediately
		int i = 1;
		if (!isDigit(i))
			return false;
		while (isDigit(i))
			i++;
		while (isWhitespace(i))
			i++;
		if (!isDot(i))
			return false;
		i++;
		while (isWhitespace(i))
			i++;
		return isLetter(i);
	}

	private boolean isDot(int i) {
		return la(i) == '.';
	}

	private boolean isLetter(int i) {
		return letters.indexOf(la(i)) != -1;
	}

	private boolean isWhitespace(int i) {
		return whiteSpaces.indexOf(la(i)) != -1;
	}

	private boolean isDigit(int i) {
		return digits.indexOf(la(i)) != -1;
	}

	private char la(int i) {		
		return (char) input.LA(i);
	}
}
