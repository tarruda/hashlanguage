package hash.parsing;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenSource;
import org.antlr.runtime.UnbufferedTokenStream;

public class ParserFactory {

	public static TokenSource createReplLexer(CharStream input) {
		return new LinefeedFilter(new UnbufferedTokenStream(
				new ConcreteHashLexer(input)), true);
	}

	public static TokenSource createLexer(CharStream input) {
		return new LinefeedFilter(new UnbufferedTokenStream(
				new ConcreteHashLexer(input)), false);
	}

	public static HashParser createParser(TokenSource source) {
		return new ConcreteHashParser(new CommonTokenStream(source));
	}

	public static HashParser createParser(CharStream input) {
		return createParser(createLexer(input));
	}

}
