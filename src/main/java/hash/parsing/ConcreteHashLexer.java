package hash.parsing;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.RecognizerSharedState;

public class ConcreteHashLexer extends HashLexer {
	public ConcreteHashLexer() {
	}

	public ConcreteHashLexer(CharStream input) {
		super(input, new RecognizerSharedState());
	}

	public ConcreteHashLexer(CharStream input, RecognizerSharedState state) {
		super(input, state);
	}
}
