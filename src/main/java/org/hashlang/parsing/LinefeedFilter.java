package org.hashlang.parsing;

import static org.hashlang.parsing.HashLexer.CATCH;
import static org.hashlang.parsing.HashLexer.CLASS;
import static org.hashlang.parsing.HashLexer.DO;
import static org.hashlang.parsing.HashLexer.ELSE;
import static org.hashlang.parsing.HashLexer.FINALLY;
import static org.hashlang.parsing.HashLexer.FOR;
import static org.hashlang.parsing.HashLexer.FUNCTION;
import static org.hashlang.parsing.HashLexer.IF;
import static org.hashlang.parsing.HashLexer.LCURLY;
import static org.hashlang.parsing.HashLexer.LINE;
import static org.hashlang.parsing.HashLexer.LROUND;
import static org.hashlang.parsing.HashLexer.LSQUARE;
import static org.hashlang.parsing.HashLexer.RCURLY;
import static org.hashlang.parsing.HashLexer.REPL;
import static org.hashlang.parsing.HashLexer.RROUND;
import static org.hashlang.parsing.HashLexer.RSQUARE;
import static org.hashlang.parsing.HashLexer.SCOLON;
import static org.hashlang.parsing.HashLexer.TRY;
import static org.hashlang.parsing.HashLexer.WHILE;
import static org.hashlang.parsing.HashLexer.WS;

import java.util.LinkedList;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;
import org.antlr.runtime.TokenStream;

/**
 * Filter responsible to select when to ignore linefeeds as statement
 * terminators. This class does minor parsing of lexer input in order to make
 * its decisions.
 * 
 * This class main purpose is keeping the parser grammar simple while
 * maintaining the language with a easy javascript-like syntax. It also adds
 * special tokens that helps determining when a statement has ended in REPL
 * 
 * @author Thiago de Arruda
 * 
 */
public class LinefeedFilter implements TokenSource {

	private TokenStream input;
	private LinkedList<Token> queue;
	private Token lastToken = Token.EOF_TOKEN;
	private boolean compoundStatement = false;
	private boolean repl;
	private Token replToken = new CommonToken(REPL);

	public LinefeedFilter(TokenStream input) {
		this(input, false);
	}

	public LinefeedFilter(TokenStream input, boolean repl) {
		this.input = input;
		queue = new LinkedList<Token>();
		this.repl = repl;
	}

	public Token nextToken() {
		if (queue.size() > 0)
			return getNext();
		if (eof())
			return Token.EOF_TOKEN;
		parse(true);
		repl();
		if (queue.size() > 0)
			return getNext();
		return Token.EOF_TOKEN;

	}

	private void repl() {
		// terminates each complete statement with a REPL token. Used only by
		// the REPL interpreter.
		if (!repl)
			return;
		int lastToken = this.lastToken.getType();
		if (queue.size() > 0)
			lastToken = queue.peekLast().getType();
		if (compoundStatement) {
			compoundStatement = false;
			queue.add(replToken);
		} else if (lastToken == LINE || lastToken == SCOLON) {
			queue.removeLast();
			queue.add(replToken);
		}
	}

	private Token getNext() {
		lastToken = queue.peek();
		return queue.remove();
	}

	public String getSourceName() {
		return null;
	}

	private void parse(boolean considerLinefeeds) {
		int tokenType = la();
		switch (tokenType) {
		case FUNCTION:
			compoundStatement = true;
			forwardIgnoringWsUntil(LCURLY);
			curlyBraces(true);
			return;
		case CLASS:
			compoundStatement = true;
			forwardIgnoringWsUntil(LCURLY);
			curlyBraces(false);
			return;
		case FOR:
		case WHILE:
			compoundStatement = true;
			forWhileStmt();
			break;
		case IF:
			compoundStatement = true;
			ifStmt();
			break;
		case DO:
			compoundStatement = true;
			doWhileStmt();
			break;
		case TRY:
			compoundStatement = true;
			tryStmt();
			curlyBraces(true);
			break;
		case LCURLY:
			compoundStatement = true;
			curlyBraces(false);
			break;
		case LROUND:
			compoundStatement = true;
			roundBraces(false);
			break;
		case LSQUARE:
			compoundStatement = true;
			squareBraces(false);
			break;
		default:
			if (considerLinefeeds)
				forward();
			else
				forwardAnyExcept(WS, LINE);
		}
	}

	private void forWhileStmt() {
		forward();
		forwardIgnoringWsUntil(LROUND);
		roundBraces(false);
		ignoreFollowingWhitespaces();
		if (!parseCompoundStatement())
			parseUntil(SCOLON, LINE, RCURLY);
		if (queue.peekLast().getType() == RCURLY)
			addTerminator();
	}

	private void ifStmt() {
		forward();
		forwardIgnoringWsUntil(LROUND);
		roundBraces(false);
		ignoreFollowingWhitespaces();
		if (!parseCompoundStatement()) {
			forwardIgnoringWsUntil(SCOLON, LINE);
			forward();
		}
		if (nextNonWhitespaceTokenIs(ELSE)) {
			ignoreFollowingWhitespaces();
			forward();
		}
		if (queue.peekLast().getType() == ELSE) {
			ignoreFollowingWhitespaces();
			if (!parseCompoundStatement())
				parseUntil(SCOLON, LINE, RCURLY);
		}
		if (queue.peekLast().getType() == RCURLY)
			addTerminator();
	}

	private void doWhileStmt() {
		forward();
		ignoreFollowingWhitespaces();
		if (!parseCompoundStatement())
			parseUntil(WHILE);
		forward();
		ignoreFollowingWhitespaces();
		roundBraces(false);
		addTerminator();
	}

	private void tryStmt() {
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

	private boolean parseCompoundStatement() {
		boolean rv = false;
		int lastToken = this.lastToken.getType();
		if (queue.size() > 0)
			lastToken = queue.peekLast().getType();
		if (lastToken == RCURLY)
			rv = true;
		else if (la() == LCURLY) {
			curlyBraces(true);
			rv = true;
		} else {
			// if this is a single statement do-while, it is possible that this
			// statement contains a block of code(another compound statement).
			// If this is the case, the next statement must be parsed before
			// forwarding to the 'while' part of the current do-while
			switch (la()) {
			case FOR:
			case DO:
			case WHILE:
			case IF:
			case TRY:
			case FUNCTION:
			case CLASS:
				parse(true);
				rv = true;
			}
		}
		return rv;
	}

	private void addTerminator() {
		Token next = input.LT(1);
		CommonToken t = new CommonToken(SCOLON, next.getText());
		t.setLine(next.getLine());
		t.setCharPositionInLine(next.getCharPositionInLine());
		queue.add(t);
	}

	private void curlyBraces(boolean considerLinefeeds) {
		forward();
		while (la() != RCURLY) {
			if (eof())
				break;
			parse(considerLinefeeds);
		}
		forward();
	}

	private void squareBraces(boolean considerLinefeeds) {
		forward();
		while (la() != RSQUARE) {
			if (eof())
				break;
			parse(considerLinefeeds);
		}
		forward();
	}

	private void roundBraces(boolean considerLinefeeds) {
		forward();
		while (la() != RROUND) {
			if (eof())
				break;
			parse(considerLinefeeds);
		}
		forward();
		if (nextNonWhitespaceTokenIs(LCURLY)) {
			// this can only happen on if/for/while or function expressions.
			// if/for/while are already handled in another rule, so this is here
			// for function expressions
			forwardIgnoringWsUntil(LCURLY);
			curlyBraces(true);
		}
	}

	private int nextNonWhitespaceToken() {
		int pos = 1;
		while (la(pos) == LINE || la(pos) == WS)
			pos++;
		return la(pos);
	}

	private boolean nextNonWhitespaceTokenIs(int type) {
		return nextNonWhitespaceToken() == type;
	}

	private void forward() {
		queue.add(input.LT(1));
		input.consume();
	}

	private void parseUntil(int... types) {
		while (true) {
			int actualType = la();
			boolean matched = true;
			for (int i = 0; matched && i < types.length; i++)
				matched = types[i] != actualType;
			if (!matched || actualType == Token.EOF)
				break;
			if (actualType != WS)
				parse(true);
			else
				input.consume();
		}
	}

	private void forwardIgnoringWsUntil(int... types) {
		while (true) {
			int actualType = la();
			boolean matched = true;
			for (int i = 0; matched && i < types.length; i++)
				matched = types[i] != actualType;
			if (!matched || actualType == Token.EOF)
				break;
			if (actualType != WS && actualType != LINE)
				forward();
			else
				input.consume();
		}
	}

	private void ignoreFollowingWhitespaces() {
		while (la() == WS || la() == LINE)
			input.consume();
	}

	private void forwardAnyExcept(int... types) {
		int actualType = la();
		boolean matched = true;
		for (int i = 0; matched && i < types.length; i++)
			matched = types[i] != actualType;
		if (matched)
			forward();
		else
			input.consume();
	}

	public boolean eof() {
		return la() == Token.EOF;
	}

	private int la() {
		return la(1);
	}

	private int la(int pos) {
		return input.LA(pos);
	}

}
