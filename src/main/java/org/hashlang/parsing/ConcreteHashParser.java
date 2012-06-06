package org.hashlang.parsing;


import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;
import org.hashlang.parsing.tree.CommonHashAdaptor;

public class ConcreteHashParser extends HashParser {

	public ConcreteHashParser(TokenStream input) {
		super(input, new RecognizerSharedState());
		setTreeAdaptor(new CommonHashAdaptor());
	}

	public ConcreteHashParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
		setTreeAdaptor(new CommonHashAdaptor());
	}
}
