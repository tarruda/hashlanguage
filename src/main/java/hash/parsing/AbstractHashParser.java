package hash.parsing;

import hash.parsing.HashParser.compoundStatement_return;
import hash.util.Constants;

import java.util.List;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;

/**
 * The super class of the generated parser. It is extended by the generated code
 * because of the superClass optoin in the .g file.
 * 
 * This class contains any helper functions used within the parser grammar
 * itself, as well as any overrides of the standard ANTLR Java runtime methods,
 * such as an implementation of a custom error reporting method, symbol table
 * populating methods and so on.
 * 
 * @author Jim Idle - Temporal Wave LLC - jimi@temporal-wave.com
 */
public abstract class AbstractHashParser extends Parser {
	/**
	 * Create a new parser instance, pre-supplying the input token stream.
	 * 
	 * @param input
	 *            The stream of tokens that will be pulled from the lexer
	 */
	protected AbstractHashParser(TokenStream input) {
		super(input);
	}

	/**
	 * Create a new parser instance, pre-supplying the input token stream and
	 * the shared state.
	 * 
	 * This is only used when a grammar is imported into another grammar, but we
	 * must supply this constructor to satisfy the super class contract.
	 * 
	 * @param input
	 *            The stream of tokesn that will be pulled from the lexer
	 * @param state
	 *            The shared state object created by an interconnectd grammar
	 */
	protected AbstractHashParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	/**
	 * Creates the error/warning message that we need to show users/IDEs when
	 * ANTLR has found a parsing error, has recovered from it and is now telling
	 * us that a parsing exception occurred.
	 * 
	 * @param tokenNames
	 *            token names as known by ANTLR (which we ignore)
	 * @param e
	 *            The exception that was thrown
	 */
	@Override
	public void displayRecognitionError(String[] tokenNames,
			RecognitionException e) {

		// This is just a place holder that shows how to override this method
		//
		super.displayRecognitionError(tokenNames, e);
	}

	protected Object nodeOrNull(ParserRuleReturnScope parserReturn) {
		if (parserReturn != null)
			return parserReturn.getTree();
		return new CommonTree(new CommonToken(HashParser.NULL, "null"));
	}

	protected String getConstructorId() {
		return Constants.CONSTRUCTOR;
	}

	protected String getImportFunctionId() {
		return Constants.IMPORT;
	}

	protected String getImportTargetId(List parts) {
		Tree t = (Tree) parts.get(parts.size() - 1);
		return t.getText();
	}

	protected String getImportString(List parts) {
		StringBuilder sb = new StringBuilder();
		for (Object object : parts) {
			sb.append(((Tree) object).getText());
			sb.append(".");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public abstract TreeAdaptor getTreeAdaptor();

	protected Object functionBlock(compoundStatement_return b) {
		TreeAdaptor adaptor = getTreeAdaptor();
		Object original = b.getTree();
		Object rv = adaptor.create(HashParser.FUNCTIONBLOCK, "Block");
		int len = adaptor.getChildCount(original);
		for (int i = 0; i < len; i++)
			adaptor.addChild(rv, adaptor.getChild(original, i));
		return rv;
	}

}
