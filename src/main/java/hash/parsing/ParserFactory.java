package hash.parsing;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;

public class ParserFactory {

	public static HashParser createParser(CharStream input) {
		ConcreteHashLexer lexer = new ConcreteHashLexer(input);
		LinefeedFilter filter = new LinefeedFilter(new CommonTokenStream(lexer));
		return new ConcreteHashParser(new CommonTokenStream(filter));
	}
}
