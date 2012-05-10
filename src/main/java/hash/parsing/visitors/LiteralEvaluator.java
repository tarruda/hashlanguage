package hash.parsing.visitors;


import hash.parsing.visitors.nodes.Result;

import org.antlr.runtime.tree.Tree;

public class LiteralEvaluator extends AstVisitor {
	@Override
	protected Tree visitInteger(Tree node) {
		String txt = node.getText();
		Long val = Long.parseLong(txt, 16);
		if (val <= Integer.MAX_VALUE)
			return new Result(val.intValue());
		return new Result(val);
	}
}
