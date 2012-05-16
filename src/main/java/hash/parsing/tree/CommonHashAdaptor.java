package hash.parsing.tree;

import hash.parsing.HashParser;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;

public class CommonHashAdaptor extends CommonTreeAdaptor {
	public Object create(Token token) {
		return new CommonHashNode(token);
	}

	@Override
	public void addChild(Object t, Object child) {
		if (((Tree) t).getType() == HashParser.IDENTIFIER)
			((HashNode) t).setNodeData(HashNode.CONTEXT_LEVEL, child);
		else
			super.addChild(t, child);
	}

}
