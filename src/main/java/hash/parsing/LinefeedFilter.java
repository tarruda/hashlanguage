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
import static hash.parsing.HashLexer.LINE;
import static hash.parsing.HashLexer.LROUND;
import static hash.parsing.HashLexer.LSQUARE;
import static hash.parsing.HashLexer.RCURLY;
import static hash.parsing.HashLexer.REPL;
import static hash.parsing.HashLexer.RROUND;
import static hash.parsing.HashLexer.RSQUARE;
import static hash.parsing.HashLexer.SCOLON;
import static hash.parsing.HashLexer.TRY;
import static hash.parsing.HashLexer.WHILE;
import static hash.parsing.HashLexer.WS;

import java.util.LinkedList;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;
import org.antlr.runtime.TokenStream;

/**
 * Filter responsible to select when to ignore/include linefeeds as statement
 * terminators. This class does minor parsing of lexer input in order to make
 * its decisions.
 * 
 * This class main purpose is keeping the parser grammar simple while
 * maintaining the language with a easy javascript-like syntax.
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
		parse(true, true);
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

	private void parse(boolean considerLinefeeds,
			boolean terminatePreviousStatements) {
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
			forWhileStmt(terminatePreviousStatements);
			break;
		case IF:
			compoundStatement = true;
			ifStmt(terminatePreviousStatements);
			break;
		case DO:
			compoundStatement = true;
			doWhileStmt(terminatePreviousStatements);
			break;
		case TRY:
			compoundStatement = true;
			tryStmt(terminatePreviousStatements);
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

	private void forWhileStmt(boolean terminatePreviousStatements) {
		if (terminatePreviousStatements)
			terminatePreviousStatement();
		forward();
		forwardIgnoringWsUntil(LROUND);
		roundBraces(false);
		ignoreFollowingWhitespaces();
		if (!parseCompoundStatement())
			parseUntil(SCOLON, LINE, FOR, DO, WHILE, IF, TRY, FUNCTION, CLASS,
					RCURLY);
		if (queue.peekLast().getType() == RCURLY)
			addTerminator();
	}

	private void ifStmt(boolean terminatePreviousStatements) {
		if (terminatePreviousStatements)
			terminatePreviousStatement();
		forward();
		forwardIgnoringWsUntil(LROUND);
		roundBraces(false);
		ignoreFollowingWhitespaces();
		if (!parseCompoundStatement()) {
			forwardIgnoringWsUntil(SCOLON, LINE, ELSE);
			if (la() == ELSE)
				// the statement wasnt terminated
				addTerminator();
			ignoreFollowingWhitespaces();
			forward();
		}
		if (nextNonWhitespaceTokenIs(ELSE)) {
			ignoreFollowingWhitespaces();
			forward();
		}
		if (queue.peekLast().getType() == ELSE) {
			ignoreFollowingWhitespaces();
			if (!parseCompoundStatement())
				parseUntil(SCOLON, LINE, FOR, DO, WHILE, IF, TRY, FUNCTION,
						CLASS, RCURLY);
		}
		if (queue.peekLast().getType() == RCURLY)
			addTerminator();
	}

	private void doWhileStmt(boolean terminatePreviousStatements) {
		if (terminatePreviousStatements)
			terminatePreviousStatement();
		forward();
		ignoreFollowingWhitespaces();
		if (!parseCompoundStatement())
			parseUntil(WHILE);
		forward();
		ignoreFollowingWhitespaces();
		roundBraces(false);
		addTerminator();
	}

	private void tryStmt(boolean terminatePreviousStatements) {
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
				parse(true, false);
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

	private void terminatePreviousStatement() {
		// terminates the previous statement if needed
		boolean terminateLastStatement = true;
		Token last = queue.peekLast();
		if (last == null)
			last = lastToken;
		switch (last.getType()) {
		case SCOLON:
		case LINE:
			terminateLastStatement = false;
		}
		if (terminateLastStatement)
			addTerminator();
	}

	private void curlyBraces(boolean considerLinefeeds) {
		// if (!considerLinefeeds
		// && (queue.size() > 0 && queue.peekLast().getType() == RROUND)
		// || lastToken.getType() == RROUND)
		// // The only way a left curly can appear after a closing round brace
		// // is in a function expression or if/for/while statement, so we
		// // force the filter to consider line feeds at this point
		// considerLinefeeds = true;
		forward();
		while (la() != RCURLY) {
			if (eof())
				break;
			parse(considerLinefeeds, true);
		}
		forward();
	}

	private void squareBraces(boolean considerLinefeeds) {
		forward();
		while (la() != RSQUARE) {
			if (eof())
				break;
			parse(considerLinefeeds, true);
		}
		forward();
	}

	private void roundBraces(boolean considerLinefeeds) {
		forward();
		while (la() != RROUND) {
			if (eof())
				break;
			parse(considerLinefeeds, true);
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
				parse(true, true);
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
