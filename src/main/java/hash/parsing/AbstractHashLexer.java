package hash.parsing;

import static hash.parsing.HashLexer.ASSIGN;
import static hash.parsing.HashLexer.BIT_AND;
import static hash.parsing.HashLexer.BIT_OR;
import static hash.parsing.HashLexer.COLON;
import static hash.parsing.HashLexer.COMMA;
import static hash.parsing.HashLexer.EOF;
import static hash.parsing.HashLexer.LCURLY;
import static hash.parsing.HashLexer.LINES;
import static hash.parsing.HashLexer.LROUND;
import static hash.parsing.HashLexer.LSQUARE;
import static hash.parsing.HashLexer.NOT;
import static hash.parsing.HashLexer.RETURN;
import static hash.parsing.HashLexer.SCOLONS;
import hash.parsing.exceptions.ParsingException;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;

@SuppressWarnings("serial")
public abstract class AbstractHashLexer extends Lexer {
	private static final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "abcdefghijklmnopqrstuvwxyz" + "_$";
	private static final String digits = "0123456789";
	private static final String whiteSpaces = " \n\t\r";		
	private int lastMatchedToken = Token.EOF;
	private boolean eof = false;

	public AbstractHashLexer() {

	}

	public AbstractHashLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}

	public AbstractHashLexer(CharStream input, RecognizerSharedState state) {
		super(input, state);

	}

	@Override
	public void emit(Token t) {
		super.emit(t);
		if (t.getChannel() == HIDDEN)
			return;
		lastMatchedToken = t.getType();
	}

	@Override
	public void emitErrorMessage(String msg) {
		// Override to send messages to another location
		super.emitErrorMessage(msg);
	}

	@Override
	public void displayRecognitionError(String[] tokenNames,
			RecognitionException e) {
		//super.displayRecognitionError(tokenNames, e);		
		throw new ParsingException(e);
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

	protected boolean hereDoc() {
		int i = nextCharIndex();
		StringBuilder buffer = new StringBuilder();
		while (!isWhitespace(i)) {
			buffer.appendCodePoint(la(i));
			i++;
		}
		if (eof)
			return false;
		i++;
		char[] delimiter = buffer.toString().toCharArray();
		buffer = new StringBuilder();
		while (true) {
			int c = la(i);
			if (eof)
				return false;
			buffer.appendCodePoint(c);
			i++;
			if (matches(delimiter, i))
				break;
		}
		input.seek(i);
		setText(buffer.toString());
		return true;
	}

	protected boolean indentedHereDoc() {
		int i = nextCharIndex();
		int indent = firstCharPositionInLine();
		StringBuilder buffer = new StringBuilder();
		while (!isWhitespace(i)) {
			buffer.appendCodePoint(la(i));
			i++;
		}
		if (eof)
			return false;
		char[] delimiter = buffer.toString().toCharArray();
		while (la(i) != '\n') {
			if (eof)
				return false;
			i++;
		}
		i += indent + 1;
		buffer = new StringBuilder();
		while (true) {
			int c = la(i);
			if (eof)
				return false;
			buffer.appendCodePoint(c);
			i++;
			if (la(i) == '\n') {
				buffer.appendCodePoint('\n');
				i += indent + 1;
				if (matches(delimiter, i))
					break;
			}
		}
		input.seek(i);
		setText(buffer.toString());
		return true;
	}

	public int firstCharPositionInLine() {
		int i = -1;
		while (la(i) != '\n' && la(i) != CharStream.EOF)
			i--;
		i++;
		return nextCharIndex(i) - i;
	}

	private int nextCharIndex() {
		return nextCharIndex(1);
	}

	private int nextCharIndex(int startIndex) {
		int i = startIndex;
		while (isWhitespace(i))
			i++;
		return i;
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

	private int la(int i) {
		if (input.LA(i) == CharStream.EOF && i > 0) {
			state.type = CharStream.EOF;
			emit();
			eof = true;
		}
		return input.LA(i);
	}

	private boolean matches(char[] s, int i) {
		boolean matches = true;
		int idx = 0;
		while (matches && idx < s.length) {
			matches = la(i) == s[idx];
			i++;
			idx++;
		}
		return matches;
	}

	protected boolean regexTokenAllowed() {
		switch (lastMatchedToken) {
		case ASSIGN:
		case SCOLONS:
		case LINES:
		case COMMA:
		case COLON:
		case LROUND:
		case LSQUARE:
		case LCURLY:
		case RETURN:
		case NOT:
		case BIT_AND:
		case BIT_OR:
		case EOF:
			return true;
		}
		return false;
	}
}
