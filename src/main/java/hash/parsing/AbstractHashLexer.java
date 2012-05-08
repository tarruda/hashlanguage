package hash.parsing;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

@SuppressWarnings("serial")
public abstract class AbstractHashLexer extends Lexer {

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

	protected void validateIntegerRange(int radix, String text) throws RecognitionException {
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
}
