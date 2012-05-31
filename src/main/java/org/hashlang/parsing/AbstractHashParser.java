package org.hashlang.parsing;

import static org.hashlang.parsing.HashParser.NAMEREF;

import java.io.PrintStream;
import java.util.List;

import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;
import org.hashlang.parsing.HashParser;
import org.hashlang.parsing.HashParser.block_return;
import org.hashlang.parsing.HashParser.statement_return;
import org.hashlang.parsing.exceptions.ParsingException;
import org.hashlang.util.Constants;

public abstract class AbstractHashParser extends Parser {

	private PrintStream err = System.err;

	protected AbstractHashParser(TokenStream input) {
		super(input);
	}

	protected AbstractHashParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	public void setErr(PrintStream err) {
		this.err = err;
	}

	@Override
	public void emitErrorMessage(String msg) {
		err.println(msg);
	}

	@Override
	public void displayRecognitionError(String[] tokenNames,
			RecognitionException e) {
		String hdr = getErrorHeader(e);
		String msg = getErrorMessage(e, tokenNames);
		throw new ParsingException(hdr + " " + msg);
	}

	public abstract TreeAdaptor getTreeAdaptor();

	public abstract void setTreeAdaptor(TreeAdaptor adaptor);

	protected Object nameRefOrNull(Token t) {
		if (t != null)
			return getTreeAdaptor().create(NAMEREF, t.getText());
		return getTreeAdaptor().create(HashParser.NULL, "null");
	}

	protected Object nodeOrNull(Token t) {
		if (t != null)
			return getTreeAdaptor().create(t);
		return getTreeAdaptor().create(HashParser.NULL, "null");
	}

	protected Object nodeOrNull(ParserRuleReturnScope parserReturn) {
		if (parserReturn != null)
			return parserReturn.getTree();
		return getTreeAdaptor().create(HashParser.NULL, "null");
	}

	protected Object block(block_return b, statement_return s) {
		TreeAdaptor adaptor = getTreeAdaptor();
		if (b != null)
			return b.tree;
		if (s != null) {
			Object rv = adaptor.create(HashParser.BLOCK, "Statement");
			adaptor.addChild(rv, s.tree);
			return rv;
		}
		return adaptor.create(HashParser.NULL, "null");
	}
	
	protected String getContainsId() {
		return Constants.CONTAINS;
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
		Object lastPart = parts.get(parts.size() - 1);
		if (lastPart instanceof Tree)
			return ((Tree) lastPart).getText();
		else
			return ((Token) lastPart).getText();
	}

	protected String getImportString(List parts) {
		StringBuilder sb = new StringBuilder();
		for (Object part : parts) {
			if (part instanceof Tree)
				sb.append(((Tree) part).getText());
			else
				sb.append(((Token) part).getText());
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
						param instanceof Token ? ((Token) param).getText()
								: adaptor.getText(param)));
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

	protected Object getTokenList(List vars) {
		TreeAdaptor adaptor = getTreeAdaptor();
		Tree rv = (Tree) adaptor.create(HashParser.LIST, "Vars");
		for (Object tok : vars) 
			rv.addChild((Tree) adaptor.create((Token) tok));		
		return rv;
	}

}
