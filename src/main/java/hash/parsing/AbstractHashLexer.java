package hash.parsing;

import hash.parsing.exceptions.ParsingException;

import java.util.Stack;

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
	private Stack<Integer> blockNesting;
	private boolean eof = false;

	public AbstractHashLexer() {
		resetLexer();
	}

	public AbstractHashLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}

	public AbstractHashLexer(CharStream input, RecognizerSharedState state) {
		super(input, state);
		resetLexer();
	}

	private void resetLexer() {
		blockNesting = new Stack<Integer>();
		blockNesting.push(0);
		eof = false;
	}

	@Override
	public void emitErrorMessage(String msg) {
		// Override to send messages to another location
		super.emitErrorMessage(msg);
	}

	@Override
	public void displayRecognitionError(String[] tokenNames,
			RecognitionException e) {
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
			ex.charPositionInLine = firstCharPositionInLine();
			throw ex;
		}
	}

	protected void incNesting() {
		int nesting = blockNesting.pop();
		nesting++;
		blockNesting.push(nesting);
	}

	protected void decNesting() {
		int nesting = blockNesting.pop();
		nesting--;
		blockNesting.push(nesting);
	}

	protected void enterBlock() {
		if (previousCharIs(')'))
			blockNesting.push(0);
		else
			incNesting();
	}

	protected void leaveBlock() {
		if (blockNesting.peek() == 0)
			blockNesting.pop();
		else
			decNesting();
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

	protected void emitTerminatorOrWhitespace() {
		char current = getText().charAt(0);
		if (current == ';'
				|| (blockNesting.peek() == 0 && (current == '\n' || current == '\r')))
			state.type = HashLexer.STERM;
		else {
			state.type = HashLexer.WS;
			state.channel = HIDDEN;
		}
		emit();
	}

	protected void emitHereDocString() {

	}

	protected void emitIndentedHereDocString() {
		int i = nextCharIndex();
		int indent = firstCharPositionInLine();
		StringBuilder buffer = new StringBuilder();
		while (!isWhitespace(i)) {
			buffer.appendCodePoint(la(i));
			i++;
		}
		if (eof)
			return;
		char[] delimiter = buffer.toString().toCharArray();
		while (la(i) != '\n') {
			i++;
			if (eof)
				return;
		}
		i += indent + 1;
		buffer = new StringBuilder();
		while (true) {
			int c = la(i);
			if (eof)
				return;
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
		state.type = HashLexer.SQ_STRING;
		setText(buffer.toString());
		emit();
	}

	public int firstCharPositionInLine() {
		int i = -2;
		while (la(i) != '\n' && la(i) != CharStream.EOF)
			i--;
		i++;
		return nextCharIndex(i) - i;
	}

	private int previousCharIndex() {
		return previousCharIndex(-2);
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

	private int previousCharIndex(int startIndex) {
		int i = startIndex;
		while (isWhitespace(i))
			i--;
		return i;
	}

	private boolean previousCharIs(char c) {
		return la(previousCharIndex()) == c;
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

}
