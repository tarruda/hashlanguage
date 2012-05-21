package hash.parsing;

import static hash.parsing.HashLexer.CATCH;
import static hash.parsing.HashLexer.CLASS;
import static hash.parsing.HashLexer.DO;
import static hash.parsing.HashLexer.ELSE;
import static hash.parsing.HashLexer.FINALLY;
import static hash.parsing.HashLexer.FOR;
import static hash.parsing.HashLexer.FUNCTION;
import static hash.parsing.HashLexer.IF;
import static hash.parsing.HashLexer.LCURLY;
import static hash.parsing.HashLexer.LINES;
import static hash.parsing.HashLexer.LROUND;
import static hash.parsing.HashLexer.LSQUARE;
import static hash.parsing.HashLexer.RCURLY;
import static hash.parsing.HashLexer.RROUND;
import static hash.parsing.HashLexer.RSQUARE;
import static hash.parsing.HashLexer.SCOLONS;
import static hash.parsing.HashLexer.TRY;
import static hash.parsing.HashLexer.WHILE;
import static hash.parsing.HashLexer.WS;

import java.util.LinkedList;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;
import org.antlr.runtime.TokenStream;

public class LinefeedFilter implements TokenSource {

	private TokenStream input;
	private LinkedList<Token> queue;
	private int lastToken = Token.EOF;

	public LinefeedFilter(TokenStream input) {
		this.input = input;
		queue = new LinkedList<Token>();
	}

	public Token nextToken() {
		if (queue.size() > 0)
			return getNext();
		if (eof())
			return Token.EOF_TOKEN;
		parse(true);
		if (queue.size() > 0)
			return getNext();
		return Token.EOF_TOKEN;
	}

	private Token getNext() {
		lastToken = queue.peek().getType();
		return queue.remove();
	}

	public String getSourceName() {
		return null;
	}

	private void parse(boolean considerLinefeeds) {
		int tokenType = input.LA(1);
		switch (tokenType) {
		case FUNCTION:
		case CLASS:
			forwardIgnoringWsUntil(LCURLY);
			return;
		case FOR:
		case WHILE:
		case IF:
			keywordParenthesisBlock();
			break;
		case ELSE:
		case DO:
			keywordBlock();
			break;
		case TRY:
			tryStatement();
			break;
		case LCURLY:
			curlyBraces(false);
			break;
		case LROUND:
			roundBraces(false);
			break;
		case LSQUARE:
			squareBraces(false);
			break;
		default:
			if (considerLinefeeds)
				forward();
			else
				forwardAnyExcept(WS, LINES);
		}
	}

	private void keywordParenthesisBlock() {
		forward();
		forwardIgnoringWsUntil(LROUND);
		roundBraces(false);
		ignoreFollowingWhitespaces();
	}

	private void keywordBlock() {
		boolean terminateLastStatement = true;
		if (queue.size() > 0)
			switch (queue.peekLast().getType()) {
			case SCOLONS:
			case LINES:
			case RCURLY:
				terminateLastStatement = false;
			}
		else
			switch (lastToken) {
			case SCOLONS:
			case LINES:
			case RCURLY:
				terminateLastStatement = false;
			}
		if (terminateLastStatement)
			queue.add(new CommonToken(SCOLONS));
		forward();
		ignoreFollowingWhitespaces();
	}

	private void curlyBraces(boolean considerLinefeeds) {
		if ((queue.size() > 0 && queue.peekLast().getType() == RROUND)
				|| lastToken == RROUND)
			// The only way a left curly can appear after a closing round brace
			// is in a function expression or if/for/while statement, so we
			// force the filter to consider line feeds at this point
			considerLinefeeds = true;
		forward();
		while (input.LA(1) != RCURLY) {
			if (eof())
				break;
			parse(considerLinefeeds);
		}
		forward();
	}

	private void squareBraces(boolean considerLinefeeds) {
		forward();
		while (input.LA(1) != RSQUARE) {
			if (eof())
				break;
			parse(considerLinefeeds);
		}
		forward();
	}

	private void roundBraces(boolean considerLinefeeds) {
		forward();
		while (input.LA(1) != RROUND) {
			if (eof())
				break;
			parse(considerLinefeeds);
		}
		forward();
		if (nextNonWhitespaceTokenIs(LCURLY))
			// this can only happen on if/for/while or function expressions.
			// if/for/while are already handled in another rule, so this is here
			// for function expressions
			forwardIgnoringWsUntil(LCURLY);
	}

	private void tryStatement() {
		forwardIgnoringWsUntil(LCURLY);
		curlyBraces(true);
		// find catch or finally
		while (nextNonWhitespaceTokenIs(CATCH)) {
			forwardIgnoringWsUntil(LCURLY);
			curlyBraces(true);
		}
		if (nextNonWhitespaceTokenIs(FINALLY)) {
			forwardIgnoringWsUntil(LCURLY);
			curlyBraces(true);
		}
	}

	private boolean nextNonWhitespaceTokenIs(int type) {
		int pos = 1;
		while (input.LA(pos) == LINES || input.LA(pos) == WS)
			pos++;
		return input.LA(pos) == type;
	}

	private void forward() {
		queue.add(input.get(input.index()));
		input.consume();
	}

	private void forwardIgnoringWsUntil(int... types) {
		while (true) {
			int actualType = input.LA(1);
			boolean matched = true;
			for (int i = 0; matched && i < types.length; i++)
				matched = types[i] != actualType;
			if (!matched || actualType == Token.EOF)
				break;
			if (actualType != WS && actualType != LINES)
				forward();
			else
				input.consume();
		}
	}

	private void ignoreFollowingWhitespaces() {
		while (input.LA(1) == WS || input.LA(1) == LINES)
			input.consume();
	}

	private void forwardAnyExcept(int... types) {
		int actualType = input.LA(1);
		boolean matched = true;
		for (int i = 0; matched && i < types.length; i++)
			matched = types[i] != actualType;
		if (matched)
			forward();
		else
			input.consume();
	}

	public boolean eof() {
		return input.LA(1) == Token.EOF;
	}
}
