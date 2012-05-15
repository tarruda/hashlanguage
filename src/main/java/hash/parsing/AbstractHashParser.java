package hash.parsing;

import hash.parsing.HashParser
.block_return;
import hash.util.Constants;

import java.util.List;

import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;

public abstract class AbstractHashParser extends Parser {

	protected AbstractHashParser(TokenStream input) {
		super(input);
	}

	protected AbstractHashParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override
	public void emitErrorMessage(String msg) {
		// Override to send messages to another location
		super.emitErrorMessage(msg);
	}

	@Override
	public void displayRecognitionError(String[] tokenNames,
			RecognitionException e) {
		super.displayRecognitionError(tokenNames, e);
		throw new RuntimeException(e);
	}

	public abstract TreeAdaptor getTreeAdaptor();

	protected Object nodeOrNull(ParserRuleReturnScope parserReturn) {
		if (parserReturn != null)
			return parserReturn.getTree();
		return getTreeAdaptor().create(HashParser.NULL, "null");
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

	protected Object functionBlock(block_return b) {
		TreeAdaptor adaptor = getTreeAdaptor();
		Object original = b.getTree();
		Object rv = adaptor.create(HashParser.FUNCTIONBLOCK, "Block");
		int len = adaptor.getChildCount(original);
		for (int i = 0; i < len; i++)
			adaptor.addChild(rv, adaptor.getChild(original, i));
		return rv;
	}

	protected Object stringList(List params) {
		TreeAdaptor adaptor = getTreeAdaptor();
		Tree rv = (Tree) adaptor.create(HashParser.LIST, "Parameters");
		if (params != null)
			for (Object param : params)
				rv.addChild((Tree) adaptor.create(HashParser.STRING,
						adaptor.getText(param)));
		return rv;
	}
}
