package hash.parsing;

import hash.parsing.tree.CommonHashAdaptor;

import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;

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
