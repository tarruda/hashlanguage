package hash.parsing;

import hash.parsing.HashParser.block_return;
import hash.parsing.exceptions.ParsingException;
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
		throw new ParsingException(e);
	}

	public abstract TreeAdaptor getTreeAdaptor();

	public abstract void setTreeAdaptor(TreeAdaptor adaptor);

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

	protected String getClassFunctionId() {
		return Constants.CLASS;
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

	protected Object tryBlock(block_return tb) {
		TreeAdaptor adaptor = getTreeAdaptor();
		Tree rv = (Tree) tb.tree;
		adaptor.setText(rv, "Try");
		return rv;
	}

	protected Object finallyBlock(block_return fb) {
		TreeAdaptor adaptor = getTreeAdaptor();
		if (fb == null)
			return adaptor.create(HashParser.NULL, "Finally");
		Tree rv = (Tree) fb.tree;
		adaptor.setText(rv, "Finally");
		return rv;
	}

	protected Object catchBlocks() {
		return catchBlocks(null);
	}

	protected Object catchBlocks(List list) {
		TreeAdaptor adaptor = getTreeAdaptor();
		Tree rv = (Tree) adaptor.create(HashParser.CATCH, "Catch Blocks");
		if (list != null)
			for (Object catchBlock : list)
				rv.addChild((Tree) catchBlock);
		return rv;
	}

}
