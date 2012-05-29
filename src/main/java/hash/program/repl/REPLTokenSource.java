package hash.program.repl;

import static hash.parsing.HashLexer.REPL;
import hash.parsing.ParserFactory;
import hash.util.Err;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Scanner;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;

public class REPLTokenSource implements TokenSource {

	private LinkedList<Token> tokenBuffer;
	private Scanner in;
	private PrintStream out;
	private Thread tokenReader;

	public REPLTokenSource(InputStream in, PrintStream out) {
		this.tokenBuffer = new LinkedList<Token>();
		this.in = new Scanner(in);
		this.out = out;
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
						.createReplLexer(new ANTLRStringStream(inputBuffer
								.toString()));
				Token t = innerSource.nextToken();
				int type = t.getType();
				while (type != Token.EOF) {
					if (type == REPL)
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
				} else 				
					out.print("... ");				
			}
		}
	}
}
