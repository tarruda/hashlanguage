package hash.repl;

import static hash.parsing.HashLexer.LINE;
import static hash.parsing.HashLexer.SCOLON;
import hash.parsing.ParserFactory;
import hash.util.Err;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.Scanner;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;

public class REPLTokenSource implements TokenSource {

	private LinkedList<Token> tokenBuffer;
	private Scanner in;
	private Thread tokenReader;

	public REPLTokenSource(InputStream in) {
		this.tokenBuffer = new LinkedList<Token>();
		this.in = new Scanner(in);
		tokenReader = new Thread(new TokenReader());
		tokenReader.setDaemon(true);
		tokenReader.start();
	}

	public Token nextToken() {
		synchronized (tokenBuffer) {
			if (tokenBuffer.size() == 0)
				try {
					tokenBuffer.wait();
				} catch (InterruptedException e) {
					throw Err.ex(e);
				}
			return tokenBuffer.remove();
		}
	}

	public String getSourceName() {
		return "REPL";
	}

	private class TokenReader implements Runnable {
		public void run() {
			StringBuilder inputBuffer = new StringBuilder();
			while (true) {
				LinkedList<Token> tempBuffer = new LinkedList<Token>();
				boolean completedStatement = false;
				try {
					inputBuffer.append(in.nextLine() + "\n");
				} catch (Throwable ex) {
					break;
				}
				TokenSource innerSource = ParserFactory
						.createLexer(new ANTLRStringStream(inputBuffer
								.toString()));
				Token t = innerSource.nextToken();
				int type = t.getType();
				while (type != Token.EOF) {
					if (type == SCOLON || type == LINE)
						completedStatement = true;
					tempBuffer.add(t);
					t = innerSource.nextToken();
					type = t.getType();
				}
				if (completedStatement) {
					synchronized (tokenBuffer) {
						inputBuffer = new StringBuilder();
						tokenBuffer.addAll(tempBuffer);
						tokenBuffer.add(Token.EOF_TOKEN);
						tokenBuffer.add(Token.EOF_TOKEN);
						tokenBuffer.notify();
					}
				}
			}
		}
	}
}
