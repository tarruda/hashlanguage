package org.hashlang.parsing;


import org.antlr.runtime.CharStream;
import org.antlr.runtime.RecognizerSharedState;
import org.hashlang.parsing.HashLexer;

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
