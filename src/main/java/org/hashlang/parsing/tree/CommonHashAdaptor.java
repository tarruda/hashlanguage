package org.hashlang.parsing.tree;

import static org.hashlang.parsing.HashParser.BLOCK;
import static org.hashlang.parsing.HashParser.NAMEREF;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;

public class CommonHashAdaptor extends CommonTreeAdaptor {
	public Object create(Token token) {
		return new CommonHashNode(token);
	}

	@Override
	public void addChild(Object t, Object child) {
		if (((Tree) t).getType() == NAMEREF)
			((HashNode) t).setNodeData(HashNode.CONTEXT_LEVEL, child);
		else
			super.addChild(t, child);
	}

	@Override
	public void setText(Object t, String text) {
		((HashNode) t).setText(text);
	}

	public static HashNode createBlock(Tree... children) {
		HashNode rv = new CommonHashNode(BLOCK);
		for (Tree stmt : children) 
			rv.addChild(stmt);
		return rv;
	}
}
