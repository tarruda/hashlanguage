package hash.parsing.visitors;

import static hash.parsing.HashParser.BINARY;
import static hash.parsing.HashParser.INTEGER;

import hash.runtime.Lookup;

import org.antlr.runtime.tree.Tree;

public class Evaluator {

	public Object evaluate(Tree node) {
		int nodeType = node.getType();
		switch (nodeType) {
		case BINARY:
			return evaluateBinaryExpression(node, node.getChild(0),
					node.getChild(1));
		case INTEGER:
			return parseInteger(node);
		default:
			return null;
		}
	}

	private Object parseInteger(Tree node) {
		String txt = node.getText();
		Long val = Long.parseLong(txt, 16);
		if (val <= Integer.MAX_VALUE)
			return val.intValue();
		return val;
	}

	private Object evaluateBinaryExpression(Tree operator, Tree lhs, Tree rhs) {
		Object left = evaluate(lhs);
		Object right = evaluate(rhs);
		return Lookup.invokeBinaryOperator(operator.getText(), left, right);
	}
}
