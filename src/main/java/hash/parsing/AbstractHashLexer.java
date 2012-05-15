package hash.parsing;

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

	public AbstractHashLexer() {
		resetNesting();
	}

	public AbstractHashLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}

	public AbstractHashLexer(CharStream input, RecognizerSharedState state) {
		super(input, state);
		resetNesting();
	}

	private void resetNesting() {
		blockNesting = new Stack<Integer>();
		blockNesting.push(0);
	}

	@Override
	public void emitErrorMessage(String msg) {
		// Override to send messages to another location
		super.emitErrorMessage(msg);
	}

	@Override
	public void displayRecognitionError(String[] tokenNames,
			RecognitionException e) {
		super.displayRecognitionError(tokenNames, e);
		throw new RuntimeException(e);
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

	protected void emitTerminatorOrWhitespace() throws RecognitionException {
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

	private boolean previousCharIs(char c) {
		int i = -2;
		while (isWhitespace(i))
			i--;
		return la(i) == c;
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
